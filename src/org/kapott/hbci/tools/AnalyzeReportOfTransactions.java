
/*  $Id: AnalyzeReportOfTransactions.java,v 1.1 2011/05/04 22:37:45 willuhn Exp $

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

package org.kapott.hbci.tools;

import java.util.Iterator;
import java.util.List;

import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.FileSystemClassLoader;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIExecStatus;
import org.kapott.hbci.structures.Konto;

/** <p>Tool zum Abholen und Auswerten von Kontoausz�gen, gleichzeitig
    Beispielprogramm f�r die Verwendung von <em>HBCI4Java</em>. Dieses Tool sollte nicht 
    out-of-the-box benutzt werden, da erst einige Anpassungen im Quelltext
    vorgenommen werden m�ssen. Es dient eher als Vorlage, wie <em>HBCI4Java</em>
    im konkreten Anwendungsfall eingesetzt werden kann.</p>
    <p>Im Quelltext m�ssen folgende Stellen angepasst werden:</p>
    <ul>
      <li><p>Beim Aufruf der Methode <code>HBCIUtils.init()</code> wird
          der Name eines Property-Files angegeben, in welchem alle ben�tigten
          Kernel-Parameter aufgelistet sind. Diese Datei muss erst erzeugt
          (Kopieren und Anpassen von <code>hbci.props.template</code>)
          und der Dateiname beim Aufruf angepasst werden.</p></li>
      <li><p>Zum Festlegen des abzufragenden Kontos wird zurzeit automatisch das
          erste Konto benutzt, auf welches �ber HBCI zugegriffen werden kann. Ist
          diese Information nicht verf�gbar (einige Banken senden keine Informationen
          �ber die verf�gbaren Konten), oder soll eine andere Kontoverbindung
          benutzt werden, so sind entsprechende �nderungen bei der Initialisierung
          der Variablen <code>myaccount</code> vorzunehmen.</p></li>
      <li><p>Soll der Kontoauszug nur ab einem bestimmten Zeitpunkt (und nicht alle
          verf�gbaren Daten) abgeholt werden, so ist beim Erzeugen des entsprechenden
          Auftrages das Startdatum einzustellen (im Quelltext zur Zeit auskommentiert).</p></li>
      <li><p>Au�erdem ist im Quelltext Code zur eigentlichen Auswertung der Ausz�ge
          zu implementieren. In dieser Vorlage wird nur nach einer fest codierten
          Rechnungsnummer im Verwendungszweck gesucht. Der entsprechende Abschnitt im
          Quelltext ist den eigenen Bed�rfnissen anzupassen.</p></li>
    </ul>
    <p>Anschlie�end kann der Quelltext compiliert und mit
    <pre>java&nbsp;-cp&nbsp;...&nbsp;org.kapott.hbci.tools.AnalyzeReportOfTransactions</pre>
    gestartet werden.</p>
    <p>Der Quellcode dieser Klasse zeigt die prinzipielle Benutzung von <em>HBCI4Java</em>.
       Wurde der HBCI-Zugang, der mit diesem Programm benutzt werden soll, noch nie verwendet,
       so werden alle ben�tigten Schritte zur Initialisierung der Zugangsdaten und
       Sicherheitsmedien automatisch von <em>HBCI4Java</em> durchgef�hrt. Es ist nicht
       n�tigt, f�r die Initialisierung von "frischen" Sicherheitsmedien speziellen
       Code in die HBCI-Anwendung einzubauen -- die entsprechenden Aktionen werden
       automatisch und v�llig transparent von <em>HBCI4Java</em> durchgef�hrt. Das hat
       den Vorteil, dass jede beliebige Anwendung, die <em>HBCI4Java</em> als HBCI-Bibliothek
       benutzt, gleichzeitig zum Initialisieren von HBCI-Sicherheitsmedien benutzt
       werden kann, ohne dass daf�r spezieller Programmcode n�tig w�re. Au�erdem wird dadurch
       sichergestellt, dass nur initialisierte und funktionierende HBCI-Sicherheitsmedien
       benutzt werden (weil <em>HBCI4Java</em> beim Laden eines Sicherheitsmediums automatisch
       entsprechende �berpr�fungen vornimmt).</p>*/
public final class AnalyzeReportOfTransactions 
{
    private static class MyHBCICallback
        extends HBCICallbackConsole
    {
        public void callback(HBCIPassport passport,int reason,String msg,int dataType,StringBuffer retData)
        {
            System.out.println("Callback f�r folgendes Passport: "+passport.getClientData("init").toString());
            super.callback(passport,reason,msg,dataType,retData);
        }
    }
    
    private static HBCIPassport passport;
    private static HBCIHandler  hbciHandle;

    public static void main(String[] args)
        throws Exception
    {
        // HBCI4Java initialisieren
        HBCIUtils.init(HBCIUtils.loadPropertiesFile(new FileSystemClassLoader(),"/home/stefan.palme/temp/a.props"),
                       new MyHBCICallback());

        // Nutzer-Passport initialisieren
        Object passportDescription="Passport f�r Kontoauszugs-Demo";
        passport=AbstractHBCIPassport.getInstance(passportDescription);

        try {
            // ein HBCI-Handle f�r einen Nutzer erzeugen
            String version=passport.getHBCIVersion();
            hbciHandle=new HBCIHandler((version.length()!=0)?version:"plus",passport);

            // auszuwertendes Konto automatisch ermitteln (das erste verf�gbare HBCI-Konto)
            Konto myaccount=passport.getAccounts()[0];
            // wenn der obige Aufruf nicht funktioniert, muss die abzufragende
            // Kontoverbindung manuell gesetzt werden:
            // Konto myaccount=new Konto("DE","86055592","1234567890");

            // Job zur Abholung der Kontoausz�ge erzeugen
            HBCIJob auszug=hbciHandle.newJob("KUmsAll");
            auszug.setParam("my",myaccount);
            // evtl. Datum setzen, ab welchem die Ausz�ge geholt werden sollen
            // job.setParam("startdate","21.5.2003");
            auszug.addToQueue();

            // alle Jobs in der Job-Warteschlange ausf�hren
            HBCIExecStatus ret=hbciHandle.execute();

            GVRKUms result=(GVRKUms)auszug.getJobResult();
            // wenn der Job "Kontoausz�ge abholen" erfolgreich ausgef�hrt wurde
            if (result.isOK()) {
                // kompletten kontoauszug als string ausgeben:
                System.out.println(result.toString());

                // kontoauszug durchlaufen, jeden eintrag einmal anfassen:
                
                List<UmsLine> lines=result.getFlatData();
                // int  numof_lines=lines.size();

                for (Iterator<UmsLine> j=lines.iterator(); j.hasNext(); ) { // alle Umsatzeintr�ge durchlaufen
                    UmsLine entry= j.next();

                    // f�r jeden Eintrag ein Feld mit allen Verwendungszweckzeilen extrahieren
                    List<String> usages=entry.usage;
                    // int  numof_usagelines=usages.size();

                    for (Iterator<String> k=usages.iterator(); k.hasNext(); ) { // alle Verwendungszweckzeilen durchlaufen
                        String usageline= k.next();

                        // ist eine bestimmte Rechnungsnummer gefunden (oder welche
                        // Kriterien hier auch immer anzuwenden sind), ...
                        if (usageline.equals("Rechnung 12345")) { 
                            // hier diesen Umsatzeintrag (<entry>) auswerten

                            // entry.bdate enth�lt Buchungsdatum
                            // entry.value enth�lt gebuchten Betrag
                            // entry.usage enth�lt die Verwendungszweck-zeilen
                            // mehr Informationen sie Dokumentation zu
                            //   org.kapott.hbci.GV_Result.GVRKUms
                        }
                    }
                }
                
            } else {
                // Fehlermeldungen ausgeben
                System.out.println("Job-Error");
                System.out.println(result.getJobStatus().getErrorString());
                System.out.println("Global Error");
                System.out.println(ret.getErrorString());
            }
        } finally {
            if (hbciHandle!=null) {
                hbciHandle.close();
            } else if (passport!=null) {
                passport.close();
            }
        }
    }
}
