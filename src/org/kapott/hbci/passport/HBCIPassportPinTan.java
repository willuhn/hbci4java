
/*  $Id: HBCIPassportPinTan.java,v 1.6 2012/03/13 22:07:43 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.passport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEParameterSpec;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidPassphraseException;
import org.kapott.hbci.manager.FlickerCode;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.security.Sig;

/** <p>Passport-Klasse f�r HBCI mit PIN/TAN. Dieses Sicherheitsverfahren wird erst
    in FinTS 3.0 spezifiziert, von einigen Banken aber schon mit fr�heren HBCI-Versionen
    angeboten.</p><p>
    Bei diesem Verfahren werden die Nachrichten auf HBCI-Ebene nicht mit kryptografischen
    Verfahren signiert oder verschl�sselt. Als "Signatur" werden statt dessen TANs 
    zusammen mit einer PIN verwendet. Die PIN wird dabei in <em>jeder</em> HBCI-Nachricht als
    Teil der "Signatur" eingef�gt, doch nicht alle Nachrichten ben�tigen eine TAN.
    Eine TAN wird nur bei der �bermittlung bestimmter Gesch�ftsvorf�lle ben�tigt. Welche
    GV das konkret sind, ermittelt <em>HBCI4Java</em> automatisch aus den BPD. F�r jeden GV, der
    eine TAN ben�tigt, wird diese via Callback abgefragt und in die Nachricht eingef�gt.</p><p>
    Die Verschl�sselung der Nachrichten bei der �bertragung erfolgt auf einer h�heren
    Transportschicht. Die Nachrichten werden n�mlich nicht direkt via TCP/IP �bertragen,
    sondern in das HTTP-Protokoll eingebettet. Die Verschl�sselung der �bertragenen Daten
    erfolgt dabei auf HTTP-Ebene (via SSL = HTTPS).</p><p>
    Wie auch bei {@link org.kapott.hbci.passport.HBCIPassportRDH} wird eine "Schl�sseldatei"
    verwendet. In dieser werden allerdings keine kryptografischen Schl�ssel abgelegt, sondern
    lediglich die Zugangsdaten f�r den HBCI-Server (Hostadresse, Nutzerkennung, usw.) sowie
    einige zus�tzliche Daten (BPD, UPD, zuletzt benutzte HBCI-Version). Diese Datei wird
    vor dem Abspeichern verschl�sselt. Vor dem Erzeugen bzw. erstmaligen Einlesen wird via
    Callback nach einem Passwort gefragt, aus welchem der Schl�ssel f�r die Verschl�sselung
    der Datei berechnet wird</p>*/
public class HBCIPassportPinTan
    extends AbstractPinTanPassport
{
    private String    filename;
    private SecretKey passportKey;

    private final static byte[] CIPHER_SALT={(byte)0x26,(byte)0x19,(byte)0x38,(byte)0xa7,
                                             (byte)0x99,(byte)0xbc,(byte)0xf1,(byte)0x55};
    private final static int CIPHER_ITERATIONS=987;

    public HBCIPassportPinTan(Object init,int dummy)
    {
        super(init);
    }

    public HBCIPassportPinTan(Object initObject)
    {
        this(initObject,0);

        String  header="client.passport.PinTan.";
        String  fname=HBCIUtils.getParam(header+"filename");
        boolean init=HBCIUtils.getParam(header+"init","1").equals("1");
        
        if (fname==null) {
            throw new NullPointerException("client.passport.PinTan.filename must not be null");
        }
        
        HBCIUtils.log("loading passport data from file "+fname,HBCIUtils.LOG_DEBUG);
        setFileName(fname);
        setCertFile(HBCIUtils.getParam(header+"certfile"));
        setCheckCert(HBCIUtils.getParam(header+"checkcert","1").equals("1"));
        
        setProxy(HBCIUtils.getParam(header+"proxy",""));
        setProxyUser(HBCIUtils.getParam(header+"proxyuser",""));
        setProxyPass(HBCIUtils.getParam(header+"proxypass",""));

        if (init) {
            HBCIUtils.log("loading data from file "+fname,HBCIUtils.LOG_DEBUG);
            
            if (!new File(fname).canRead()) {
                HBCIUtils.log("have to create new passport file",HBCIUtils.LOG_WARN);
                askForMissingData(true,true,true,true,true,true,true);
                saveChanges();
            }

            ObjectInputStream o=null;
            try {
                int retries=Integer.parseInt(HBCIUtils.getParam("client.retries.passphrase","3"));
                
                while (true) {
                    if (passportKey==null)
                        passportKey=calculatePassportKey(FOR_LOAD);

                    PBEParameterSpec paramspec=new PBEParameterSpec(CIPHER_SALT,CIPHER_ITERATIONS);
                    Cipher cipher=Cipher.getInstance("PBEWithMD5AndDES");
                    cipher.init(Cipher.DECRYPT_MODE,passportKey,paramspec);
                    
                    o=null;
                    try {
                        o=new ObjectInputStream(new CipherInputStream(new FileInputStream(fname),cipher));
                    } catch (StreamCorruptedException e) {
                        passportKey=null;
                        
                        retries--;
                        if (retries<=0)
                            throw new InvalidPassphraseException();
                    }
                    
                    if (o!=null)
                        break;
                }

                setCountry((String)(o.readObject()));
                setBLZ((String)(o.readObject()));
                setHost((String)(o.readObject()));
                setPort((Integer)(o.readObject()));
                setUserId((String)(o.readObject()));
                setSysId((String)(o.readObject()));
                setBPD((Properties)(o.readObject()));
                setUPD((Properties)(o.readObject()));

                setHBCIVersion((String)o.readObject());
                setCustomerId((String)o.readObject());
                setFilterType((String)o.readObject());
                
                try {
                    setAllowedTwostepMechanisms((List<String>)o.readObject());
                    try {
                        setCurrentTANMethod((String)o.readObject());
                    } catch (Exception e) {
                        HBCIUtils.log("no current secmech found in passport file - automatically upgrading to new file format", HBCIUtils.LOG_WARN);
                        // TODO: remove this
                        HBCIUtils.log("exception while reading current TAN method was:", HBCIUtils.LOG_DEBUG);
                        HBCIUtils.log(HBCIUtils.exception2String(e), HBCIUtils.LOG_DEBUG);
                    }
                } catch (Exception e) {
                    HBCIUtils.log("no list of allowed secmechs found in passport file - automatically upgrading to new file format", HBCIUtils.LOG_WARN);
                    // TODO: remove this
                    HBCIUtils.log("exception while reading list of allowed two step mechs was:", HBCIUtils.LOG_DEBUG);
                    HBCIUtils.log(HBCIUtils.exception2String(e), HBCIUtils.LOG_DEBUG);
                }
                
                // TODO: hier auch gew�hltes pintan/verfahren lesen
            } catch (Exception e) {
                throw new HBCI_Exception("*** loading of passport file failed",e);
            }

            try {
                o.close();
            } catch (Exception e) {
                HBCIUtils.log(e);
            }
            
            if (askForMissingData(true,true,true,true,true,true,true))
                saveChanges();
        }
    }
    
    /** Gibt den Dateinamen der Schl�sseldatei zur�ck.
        @return Dateiname der Schl�sseldatei */
    public String getFileName() 
    {
        return filename;
    }

    public void setFileName(String filename) 
    { 
        this.filename=filename;
    }
    
    public void resetPassphrase()
    {
        passportKey=null;
    }

    public void saveChanges()
    {
        try {
            if (passportKey==null) 
                passportKey=calculatePassportKey(FOR_SAVE);
            
            PBEParameterSpec paramspec=new PBEParameterSpec(CIPHER_SALT,CIPHER_ITERATIONS);
            Cipher cipher=Cipher.getInstance("PBEWithMD5AndDES");
            cipher.init(Cipher.ENCRYPT_MODE,passportKey,paramspec);

            File passportfile=new File(getFileName());
            File directory=passportfile.getAbsoluteFile().getParentFile();
            String prefix=passportfile.getName()+"_";
            File tempfile=File.createTempFile(prefix,"",directory);

            ObjectOutputStream o=new ObjectOutputStream(new CipherOutputStream(new FileOutputStream(tempfile),cipher));

            o.writeObject(getCountry());
            o.writeObject(getBLZ());
            o.writeObject(getHost());
            o.writeObject(getPort());
            o.writeObject(getUserId());
            o.writeObject(getSysId());
            o.writeObject(getBPD());
            o.writeObject(getUPD());

            o.writeObject(getHBCIVersion());
            o.writeObject(getCustomerId());
            o.writeObject(getFilterType());
            
            // hier auch gew�hltes zweischritt-verfahren abspeichern
            List<String> l=getAllowedTwostepMechanisms();
            // TODO: remove this
            StringBuffer sb=new StringBuffer();
            for (Iterator<String> i=l.iterator(); i.hasNext(); ) {
                sb.append(i.next()+", ");
            }
            HBCIUtils.log("saving two step mechs: "+sb, HBCIUtils.LOG_DEBUG);
            o.writeObject(l);
            
            // TODO: remove this
            String s=getCurrentTANMethod(false);
            HBCIUtils.log("saving current tan method: "+s, HBCIUtils.LOG_DEBUG);
            o.writeObject(s);

            o.close();
            passportfile.delete();
            tempfile.renameTo(passportfile);
        } catch (Exception e) {
            throw new HBCI_Exception("*** saving of passport file failed",e);
        }
    }
    
    public byte[] hash(byte[] data)
    {
        /* there is no hashing before signing, so we return the original message,
         * which will later be "signed" by sign() */
        return data;
    }
    
    public byte[] sign(byte[] data)
    {
        try {
            // TODO: wenn die eingegebene PIN falsch war, muss die irgendwie
            // resettet werden, damit wieder danach gefragt wird
            if (getPIN()==null) {
                StringBuffer s=new StringBuffer();

                HBCIUtilsInternal.getCallback().callback(this,
                                                 HBCICallback.NEED_PT_PIN,
                                                 HBCIUtilsInternal.getLocMsg("CALLB_NEED_PTPIN"),
                                                 HBCICallback.TYPE_SECRET,
                                                 s);
                if (s.length()==0) {
                    throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PINZERO"));
                }
                setPIN(s.toString());
                LogFilter.getInstance().addSecretData(getPIN(),"X",LogFilter.FILTER_SECRETS);
            }
            
            String tan="";
            
            // tan darf nur beim einschrittverfahren oder bei 
            // PV=1 und passport.contains(challenge)           und tan-pflichtiger auftrag oder bei
            // PV=2 und passport.contains(challenge+reference) und HKTAN
            // ermittelt werden
            
            String pintanMethod=getCurrentTANMethod(false);

            if (pintanMethod.equals(Sig.SECFUNC_SIG_PT_1STEP)) {
                // nur beim normalen einschritt-verfahren muss anhand der segment-
                // codes ermittelt werden, ob eine tan ben�tigt wird
                HBCIUtils.log("onestep method - checking GVs to decide whether or not we need a TAN",HBCIUtils.LOG_DEBUG);
                
                // segment-codes durchlaufen
                String codes=collectSegCodes(new String(data,"ISO-8859-1"));
                StringTokenizer tok=new StringTokenizer(codes,"|");
                
                while (tok.hasMoreTokens()) {
                    String code=tok.nextToken();
                    String info=getPinTanInfo(code);
                    
                    if (info.equals("J")) {
                        // f�r dieses segment wird eine tan ben�tigt
                        HBCIUtils.log("the job with the code "+code+" needs a TAN",HBCIUtils.LOG_DEBUG);
                        
                        if (tan.length()==0) {
                            // noch keine tan bekannt --> callback
                            
                            StringBuffer s=new StringBuffer();
                            HBCIUtilsInternal.getCallback().callback(this,
                                HBCICallback.NEED_PT_TAN,
                                HBCIUtilsInternal.getLocMsg("CALLB_NEED_PTTAN"),
                                HBCICallback.TYPE_TEXT,
                                s);
                            if (s.length()==0) {
                                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_TANZERO"));
                            }
                            tan=s.toString();
                        } else {
                            HBCIUtils.log("there should be only one job that needs a TAN!",HBCIUtils.LOG_WARN);
                        }
                        
                    } else if (info.equals("N")) {
                        HBCIUtils.log("the job with the code "+code+" does not need a TAN",HBCIUtils.LOG_DEBUG);
                        
                    } else if (info.length()==0) {
                        // TODO: ist das hier dann nicht ein A-Segment? In dem Fall
                        // w�re diese Warnung �berfl�ssig
                        HBCIUtils.log("the job with the code "+code+" seems not to be allowed with PIN/TAN",HBCIUtils.LOG_WARN);
                    }
                }
            } else {
                HBCIUtils.log("twostep method - checking passport(challenge) to decide whether or not we need a TAN",HBCIUtils.LOG_DEBUG);
                Properties secmechInfo=getCurrentSecMechInfo();
                
                // gespeicherte challenge aus passport holen
                String challenge=(String)getPersistentData("pintan_challenge");
                setPersistentData("pintan_challenge",null);
                
                // willuhn 2011-05-27 Wir versuchen, den Flickercode zu ermitteln und zu parsen
                String hhduc = (String) getPersistentData("pintan_challenge_hhd_uc");
                setPersistentData("pintan_challenge_hhd_uc",null); // gleich wieder aus dem Passport loeschen
                String flicker = parseFlickercode(challenge,hhduc);
                
                if (challenge==null) {
                    // es gibt noch keine challenge
                    HBCIUtils.log("will not sign with a TAN, because there is no challenge",HBCIUtils.LOG_DEBUG);
                } else {
                    HBCIUtils.log("found challenge in passport, so we ask for a TAN",HBCIUtils.LOG_DEBUG);
                    // es gibt eine challenge, also damit tan ermitteln
                    
                    // willuhn 2011-05-27: Flicker-Code uebergeben, falls vorhanden
                    // bei NEED_PT_SECMECH wird das auch so gemacht.
                    StringBuffer s = flicker != null ? new StringBuffer(flicker) : new StringBuffer();
                    HBCIUtilsInternal.getCallback().callback(this,
                        HBCICallback.NEED_PT_TAN,
                        secmechInfo.getProperty("name")+"\n"+secmechInfo.getProperty("inputinfo")+"\n\n"+challenge,
                        HBCICallback.TYPE_TEXT,
                        s);
                    if (s.length()==0) {
                        throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_TANZERO"));
                    }
                    tan=s.toString();
                }
            }
            if (tan.length()!=0) {
            	LogFilter.getInstance().addSecretData(tan,"X",LogFilter.FILTER_SECRETS);
            }

            return (getPIN()+"|"+tan).getBytes("ISO-8859-1");
        } catch (Exception ex) {
            throw new HBCI_Exception("*** signing failed",ex);
        }
    }
    
    /**
     * Versucht, aus Challenge und Challenge HHDuc den Flicker-Code zu extrahieren
     * und ihn in einen flickerfaehigen Code umzuwandeln.
     * Nur wenn tatsaechlich ein gueltiger Code enthalten ist, der als
     * HHDuc-Code geparst und in einen Flicker-Code umgewandelt werden konnte,
     * liefert die Funktion den Code. Sonst immer NULL.
     * @param challenge der Challenge-Text. Das DE "Challenge HHDuc" gibt es
     * erst seit HITAN4. Einige Banken haben aber schon vorher optisches chipTAN
     * gemacht. Die haben das HHDuc dann direkt im Freitext des Challenge
     * mitgeschickt (mit String-Tokens zum Extrahieren markiert). Die werden vom
     * FlickerCode-Parser auch unterstuetzt.
     * @param hhduc das echte Challenge HHDuc.
     * @return der geparste und in Flicker-Format konvertierte Code oder NULL.
     */
    private String parseFlickercode(String challenge, String hhduc)
    {
      // 1. Prioritaet hat hhduc. Gibts aber erst seit HITAN4
      if (hhduc != null && hhduc.trim().length() > 0)
      {
        try
        {
          FlickerCode code = new FlickerCode(hhduc);
          return code.render();
        }
        catch (Exception e)
        {
          HBCIUtils.log("unable to parse Challenge HHDuc " + hhduc + ":" + HBCIUtils.exception2String(e),HBCIUtils.LOG_DEBUG);
        }
      }
      
      // 2. Checken, ob im Freitext-Challenge was parse-faehiges steht.
      // Kann seit HITAN1 auftreten
      if (challenge != null && challenge.trim().length() > 0)
      {
        try
        {
          FlickerCode code = new FlickerCode(challenge);
          return code.render();
        }
        catch (Exception e)
        {
          // Das darf durchaus vorkommen, weil das Challenge auch bei manuellem
          // chipTAN- und smsTAN Verfahren verwendet wird, wo gar kein Flicker-Code enthalten ist.
          // Wir loggen es aber trotzdem - fuer den Fall, dass tatsaechlich ein Flicker-Code
          // enthalten ist. Sonst koennen wir das nicht debuggen.
          HBCIUtils.log("challenge contains no HHDuc (no problem in most cases):" + HBCIUtils.exception2String(e),HBCIUtils.LOG_DEBUG2);
        }
      }
      // Ne, definitiv kein Flicker-Code.
      return null;
    }

    public boolean verify(byte[] data,byte[] sig)
    {
        // TODO: fuer bankensignaturen fuer HITAN muss dass hier ge�ndert werden
        return true;
    }

    public byte[][] encrypt(byte[] plainMsg)
    {
        try {
            int padLength=plainMsg[plainMsg.length-1];
            byte[] encrypted=new String(plainMsg,0,plainMsg.length-padLength,"ISO-8859-1").getBytes("ISO-8859-1");
            return new byte[][] {new byte[8],encrypted};
        } catch (Exception ex) {
            throw new HBCI_Exception("*** encrypting message failed",ex);
        }
    }

    public byte[] decrypt(byte[] cryptedKey,byte[] cryptedMsg)
    {
        try {
            return new String(new String(cryptedMsg,"ISO-8859-1")+'\001').getBytes("ISO-8859-1");
        } catch (Exception ex) {
            throw new HBCI_Exception("*** decrypting of message failed",ex);
        }
    }
    
    public void close()
    {
        super.close();
        passportKey=null;
    }
    
}
