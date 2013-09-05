
/*  $Id: GVUebSEPA.java,v 1.1 2011/05/04 22:37:54 willuhn Exp $

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

package org.kapott.hbci.GV;
 
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kapott.hbci.GV.generators.ISEPAGenerator;
import org.kapott.hbci.GV.generators.SEPAGeneratorFactory;
import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.xml.XMLCreator2;
import org.kapott.hbci.xml.XMLData;

public class GVUebSEPA
    extends HBCIJobImpl
{
    private Properties sepaParams;
    
    //Pain Version die genutzt werden soll. Gr��ter gemeinsamer Nenner von Bank und HBCI4Java unterst�tzter Version
    //Sicherheitshalber mit default Value initialisieren, wird sp�ter �berschrieben
    private String painToUse = "pain.001.001.02";	 
    
    public static String getLowlevelName()
    {
        return "UebSEPA";
    }

    public GVUebSEPA(HBCIHandler handler,String name)
    {
        super(handler,name,new HBCIJobResultImpl());
        this.sepaParams = new Properties();
    }

    public GVUebSEPA(HBCIHandler handler)
    {
        this(handler,getLowlevelName());
        
        //Pr�fen welche Pain Version die Bank unterstz�tzt und diese mit den von HBCI4Java unterst�tzten Pains vergleichen
        checkSupportedPainVersion(handler);
        
        
        addConstraint("src.bic",  "My.bic",  null, LogFilter.FILTER_MOST);
        addConstraint("src.iban", "My.iban", null, LogFilter.FILTER_IDS);

        /*
        addConstraint("src.country",  "My.KIK.country", "", LogFilter.FILTER_NONE);
        addConstraint("src.blz",      "My.KIK.blz",     "", LogFilter.FILTER_MOST);
        addConstraint("src.number",   "My.number",      "", LogFilter.FILTER_IDS);
        addConstraint("src.subnumber","My.subnumber",  "", LogFilter.FILTER_MOST);
        */

        

        addConstraint("_sepadescriptor", "sepadescr", "sepade."+painToUse+".xsd", LogFilter.FILTER_NONE);
        addConstraint("_sepapain",       "sepapain",  null,                         LogFilter.FILTER_IDS);

        /* dummy constraints to allow an application to set these values. the
         * overriden setLowlevelParam() stores these values in a special structure
         * which is later used to create the SEPA pain document. */
        addConstraint("src.bic",   "sepa.src.bic",   null, LogFilter.FILTER_MOST);
        addConstraint("src.iban",  "sepa.src.iban",  null, LogFilter.FILTER_IDS);
        addConstraint("src.name",  "sepa.src.name",  null, LogFilter.FILTER_IDS);
        addConstraint("dst.bic",   "sepa.dst.bic",   null, LogFilter.FILTER_MOST);
        addConstraint("dst.iban",  "sepa.dst.iban",  null, LogFilter.FILTER_IDS);
        addConstraint("dst.name",  "sepa.dst.name",  null, LogFilter.FILTER_IDS);
        addConstraint("btg.value", "sepa.btg.value", null, LogFilter.FILTER_NONE);
        addConstraint("btg.curr",  "sepa.btg.curr",  "EUR", LogFilter.FILTER_NONE);
        addConstraint("usage",     "sepa.usage",     null, LogFilter.FILTER_NONE);
    }

    /**
     * Diese Methode schaut in den BPD nach den unterstz�tzen pain Versionen (bei UebSEPA pain.001.xxx.xx) 
     * und vergleicht diese mit den von HBCI4Java unterst�tzen pain Versionen. Der gr��te gemeinsamme Nenner
     * wird schlie�lich in this.painToUse gespeichert.  
     * @param handler
     */
    private void checkSupportedPainVersion(HBCIHandler handler) {
    	//Erst pr�fen ob die SEPAInfo �berhaupt vorhanden ist
    	if (handler.getSupportedLowlevelJobs().getProperty("SEPAInfo") !=null)
        {
    		//Regex f�r die pain Version			
			Pattern pattern = Pattern.compile("pain\\.001\\.(\\d\\d\\d\\.\\d\\d)");
			
			//Liste zum speichern aller gefundenen pain Versionen
			ArrayList<String[]> validPains = new ArrayList<String[]>();
			
			//SEPAInfo laden und dar�ber iterieren
    		Properties props = handler.getLowlevelJobRestrictions("SEPAInfo");
			Enumeration<?> e = props.propertyNames();
			while(e.hasMoreElements()){
				String key = (String) e.nextElement();
				String val = props.getProperty(key);
								
				//pain Version suchen
				Matcher m = pattern.matcher(val);
				while (m.find()) {
					//Pr�fen ob die gefundene pain Version von HBCI4Java unterst�tzt wird. Dazu einfach pr�fen ob das pain Schema vorhanden ist
					String rawpain  = m.group(1);
					URL u = GVUebSEPA.class.getClassLoader().getResource("pain.001."+rawpain+".xsd");
					if (u != null) {
						validPains.add(rawpain.split("\\."));   
					}					
				}
			}
			
			int maxMajorVersion = 0;
			int maxMinorVersion = 0;
			for(String[] pain : validPains){
				int maj = Integer.parseInt(pain[0]);
				maxMajorVersion = maj > maxMajorVersion ? maj : maxMajorVersion; 
			}
			for(String[] pain : validPains){
				int maj = Integer.parseInt(pain[0]);
				int min = Integer.parseInt(pain[1]);
				if(maj == maxMajorVersion){
					maxMinorVersion = min > maxMinorVersion ? min : maxMinorVersion;
				} 
			}
			for(String[] pain : validPains){
				int maj = Integer.parseInt(pain[0]);
				int min = Integer.parseInt(pain[1]);
				if(maxMajorVersion == maj && maxMinorVersion == min)
					painToUse = "pain.001."+pain[0]+"."+pain[1];
			}
        }
	}

	/* This is needed to "redirect" the sepa values. They dont have to stored
     * directly in the message, but have to go into the SEPA document which will
     * by created later (in verifyConstraints()) */
    protected void setLowlevelParam(String key, String value)
    {
        String intern=getName()+".sepa.";

        if (key.startsWith(intern)) {
            String realKey=key.substring(intern.length());
            this.sepaParams.setProperty(realKey, value);
            HBCIUtils.log("setting SEPA param "+realKey+" = "+value, HBCIUtils.LOG_DEBUG);
        } else {
            super.setLowlevelParam(key, value);
        }
    }


    /* This is needed for verifyConstraints(). Because verifyConstraints() tries
     * to read the lowlevel-values for each constraint, the lowlevel-values for
     * sepa.xxx would always be empty (because they do not exist in hbci messages).
     * So we read the sepa lowlevel-values from the special sepa structure instead
     * from the lowlevel params for the message */
    public String getLowlevelParam(String key)
    {
        String result;

        String intern=getName()+".sepa.";
        if (key.startsWith(intern)) {
            String realKey=key.substring(intern.length());
            result=getSEPAParam(realKey);
        } else {
            result=super.getLowlevelParam(key);
        }

        return result;
    }

    /**
     * Gibt die SEPA Message ID als String zur�ck. Existiert noch keine wird sie aus Datum und User ID erstellt. 
     * @return SEPA Message ID
     */
    public String getSEPAMessageId()
    {
        String result=getSEPAParam("messageId");
        if (result==null) {
            Date now=new Date();
            result=now.getTime() + "-" + getMainPassport().getUserId();
            result=result.substring(0, Math.min(result.length(),35));
            setSEPAParam("messageId", result);
        }
        return result;
    }

    /**
     * Erstellt einen Timestamp im ISODateTime Forma.
     * @discuss Diese methode w�re bestimmt auch gut in der SEPAGeneratorFactory oder den einzelnen Generator Klassen n�tzlich
     * @return Aktuelles Datum als ISODateTime String
     */
    public String createSEPATimestamp()
    {
        Date             now=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return format.format(now);
    }


    protected void createSEPAFromParams()
    {
    	
    	//TODO: Entfernen wenn auf JAXB umgestellt ist
        // open SEPA descriptor and create an XML-Creator using it
        /* TODO: load correct schema files depending on the SEPA descriptor set
         * above, depending on the supported SEPA descriptors (BPD) */
//    	InputStream f=this.getClass().getClassLoader().getResourceAsStream("pain.001.001.02.xsd");
//        XMLCreator2 creator=new XMLCreator2(f);
//
//        // define data to be filled into SEPA document
//        XMLData xmldata=new XMLData();
//        xmldata.setValue("Document/pain.001.001.02/GrpHdr/MsgId",                              getSEPAMessageId());
//        xmldata.setValue("Document/pain.001.001.02/GrpHdr/CreDtTm",                            createSEPATimestamp());
//        xmldata.setValue("Document/pain.001.001.02/GrpHdr/NbOfTxs",                            "1");
//        xmldata.setValue("Document/pain.001.001.02/GrpHdr/InitgPty/Nm",                        getSEPAParam("src.name"));
//
//        xmldata.setValue("Document/pain.001.001.02/PmtInf/Dbtr/Nm",                            getSEPAParam("src.name"));
//        xmldata.setValue("Document/pain.001.001.02/PmtInf/DbtrAgt/FinInstnId/BIC",             getSEPAParam("src.bic"));
//        xmldata.setValue("Document/pain.001.001.02/PmtInf/DbtrAcct/Id/IBAN",                   getSEPAParam("src.iban"));
//
//        xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/Cdtr/Nm",                getSEPAParam("dst.name"));
//        xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/CdtrAgt/FinInstnId/BIC", getSEPAParam("dst.bic"));
//        xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/CdtrAcct/Id/IBAN",       getSEPAParam("dst.iban"));
//
//        xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/Amt/InstdAmt",           getSEPAParam("btg.value"));
//        xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/Amt/InstdAmt:Ccy",       getSEPAParam("btg.curr"));
//        xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/RmtInf/Ustrd",           getSEPAParam("usage"));
//        xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/PmtId/EndToEndId",       getSEPAMessageId());
//
//        xmldata.setValue("Document/pain.001.001.02/PmtInf/ReqdExctnDt",                        "1999-01-01"); // hart kodiert

        // create SEPA document
        ByteArrayOutputStream o=new ByteArrayOutputStream();
//    	String schema = "pain.001.001.02";
    	ISEPAGenerator gen = SEPAGeneratorFactory.get(this, painToUse);
    	try{
    		gen.generate(this, o);
    	}catch(Exception e){
    		throw new HBCI_Exception("*** the _sepapain segment for this job can not be created",e);
    	}    	
    	
    	if(o.size() == 0)  		
    		throw new HBCI_Exception("*** the _sepapain segment for this job can not be created");
    	
        
//      creator.createXMLFromSchemaAndData(xmldata, o); //TODO: Entfernen wenn auf JAXB umgestellt ist

    	try {
			System.out.println("-------------" + o.toString("ISO-8859-1"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        // store SEPA document as parameter
        try {
            setParam("_sepapain", "B"+o.toString("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void verifyConstraints()
    {
        // creating SEPA document and storing it in _sepapain
        createSEPAFromParams();

        // verify all constraints
        super.verifyConstraints();

        // TODO: checkIBANCRC
    }

    protected void setSEPAParam(String name, String value)
    {
        this.sepaParams.setProperty(name, value);
    }

    /**
     * Liest den Parameter zu einem gegeben Key aus dem speziellen SEPA Parametern aus
     * @param Key
     * @return Value
     */
    public String getSEPAParam(String name)
    {
        return this.sepaParams.getProperty(name);
    }
}
