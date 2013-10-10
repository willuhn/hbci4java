
/*  $Id: HBCIKernel.java,v 1.1 2011/05/04 22:37:46 willuhn Exp $

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

package org.kapott.hbci.manager;

import java.util.Hashtable;
import java.util.List;

/** HBCI-Kernel f�r eine bestimmte HBCI-Version. Objekte dieser Klasse 
 * werden intern f�r die Nachrichtenerzeugung und -analyse verwendet. */
public interface HBCIKernel
{
    /** Gibt die HBCI-Versionsnummer zur�ck, f�r die dieses Kernel-Objekt 
     * Nachrichten erzeugen und analysieren kann.
     * @return HBCI-Versionsnummer */
    public String getHBCIVersion();

    /** <p>Gibt die Namen und Versionen aller von <em>HBCI4Java</em> f�r die
     * aktuelle HBCI-Version (siehe {@link #getHBCIVersion()}) unterst�tzten 
     * Lowlevel-Gesch�ftsvorf�lle zur�ck. Es ist zu beachten, dass ein konkreter
     * HBCI-Zugang i.d.R. nicht alle in dieser Liste aufgef�hrten 
     * Gesch�ftsvorf�lle auch tats�chlich anbietet (siehe daf�r
     * {@link HBCIHandler#getSupportedLowlevelJobs()}).</p>
     * <p>Die zur�ckgegebene Hashtable enth�lt als Key jeweils einen String mit 
     * dem Bezeichner eines Lowlevel-Jobs, welcher f�r die Erzeugung eines
     * Lowlevel-Jobs mit {@link HBCIHandler#newLowlevelJob(String)} verwendet
     * werden kann. Der dazugeh�rige Wert ist ein List-Objekt (bestehend aus 
     * Strings), welches alle GV-Versionsnummern enth�lt, die von 
     * <em>HBCI4Java</em> f�r diesen GV unterst�tzt werden.</p>
     * @return Hashtable aller Lowlevel-Jobs, die prinzipiell vom aktuellen
     * Handler-Objekt unterst�tzt werden. */
    public Hashtable<String, List<String>> getAllLowlevelJobs();

    /** <p>Gibt alle f�r einen bestimmten Lowlevel-Job m�glichen Job-Parameter-Namen
     * zur�ck. Der �bergebene Job-Name ist einer der von <em>HBCI4Java</em>
     * unterst�tzten Jobnamen, die Versionsnummer muss eine der f�r diesen GV
     * unterst�tzten Versionsnummern sein (siehe {@link #getAllLowlevelJobs()}).
     * Als Ergebnis erh�lt man eine Liste aller Parameter-Namen, die f�r einen
     * Lowlevel-Job (siehe {@link HBCIHandler#newLowlevelJob(String)}) gesetzt
     * werden k�nnen (siehe 
     * {@link org.kapott.hbci.GV.HBCIJob#setParam(String, String)}).</p>
     * <p>Aus der Liste der m�glichen Parameternamen ist nicht ersichtlich, 
     * welche Parameter zwingend und welche optional sind, bzw. wie oft ein
     * Parameter mindestens oder h�chstens auftreten darf. F�r diese Art der
     * Informationen stehen zur Zeit noch keine Methoden bereit.</p>
     * <p>Siehe dazu auch {@link HBCIHandler#getLowlevelJobParameterNames(String)}.</p>
     * @param gvname Name des Lowlevel-Jobs
     * @param version Version des Lowlevel-jobs
     * @return Liste aller Job-Parameter, die beim Erzeugen des angegebenen
     * Lowlevel-Jobs gesetzt werden k�nnen */
    public List getLowlevelJobParameterNames(String gvname,String version);

    /** <p>Gibt f�r einen bestimmten Lowlevel-Job die Namen aller
     * m�glichen Lowlevel-Result-Properties zur�ck 
     * (siehe {@link org.kapott.hbci.GV_Result.HBCIJobResult#getResultData()}).
     * Der �bergebene Job-Name ist einer der von <em>HBCI4Java</em>
     * unterst�tzten Jobnamen, die Versionsnummer muss eine der f�r diesen GV
     * unterst�tzten Versionsnummern sein (siehe {@link #getAllLowlevelJobs()}).
     * Als Ergebnis erh�lt man eine Liste aller Property-Namen, die in den
     * Lowlevel-Ergebnisdaten eines Jobs auftreten k�nnen.</p>
     * <p>Aus der resultierenden Liste ist nicht ersichtlich, 
     * welche Properties immer zur�ckgeben werden und welche optional sind, bzw. 
     * wie oft ein bestimmter Wert mindestens oder h�chstens auftreten kann. 
     * F�r diese Art der Informationen stehen zur Zeit noch keine Methoden 
     * bereit.</p>
     * <p>Siehe dazu auch {@link HBCIHandler#getLowlevelJobResultNames(String)}.</p>
     * @param gvname Name des Lowlevel-Jobs
     * @param version Version des Lowlevel-jobs
     * @return Liste aller Property-Namen, die in den Lowlevel-Antwortdaten
     * eines Jobs auftreten k�nnen */
    public List getLowlevelJobResultNames(String gvname,String version);

    /** <p>Gibt f�r einen bestimmten Lowlevel-Job die Namen aller
     * m�glichen Job-Restriction-Parameter zur�ck 
     * (siehe auch {@link org.kapott.hbci.GV.HBCIJob#getJobRestrictions()} und
     * {@link HBCIHandler#getLowlevelJobRestrictions(String)}).
     * Der �bergebene Job-Name ist einer der von <em>HBCI4Java</em>
     * unterst�tzten Jobnamen, die Versionsnummer muss eine der f�r diesen GV
     * unterst�tzten Versionsnummern sein (siehe {@link #getAllLowlevelJobs()}).
     * Als Ergebnis erh�lt man eine Liste aller Property-Namen, die in den
     * Job-Restrictions-Daten eines Jobs auftreten k�nnen.</p>
     * @param gvname Name des Lowlevel-Jobs
     * @param version Version des Lowlevel-jobs
     * @return Liste aller Property-Namen, die in den Job-Restriction-Daten
     * eines Jobs auftreten k�nnen */
    public List getLowlevelJobRestrictionNames(String gvname,String version);
}