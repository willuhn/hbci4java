
/*  $Id: GVRFestList.java,v 1.1 2011/05/04 22:37:47 willuhn Exp $

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

package org.kapott.hbci.GV_Result;

import java.util.ArrayList;
import java.util.List;

import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

/** Ergebnisse der Abfrage bestehender Festgeldanlange. Die verschiedenen Festgeldanlagen
    werden in einer Liste gespeichert. F�r jede bestehende Festgeldanlage gibt es ein separates
    Objekt mit Informationen �ber diese Anlage. */
public final class GVRFestList
    extends HBCIJobResultImpl
{
    /** Informationen �ber eine einzelne. Festgeldanlage */
    public static final class Entry
    {
        /** Informationen dar�ber, wie eine Festgeldanlage bei Ablauf der
            Laufzeit zu verl�ngern ist */
        public static final class Prolong
        {
            /** Neue Laufzeit nach dem Ablaufdatum */
            public int     laufzeit;
            /** Neuer Betrag der Anlage */
            public Value   betrag;
            /** Soll Festgeldanlage nach der zus�tzlichen <code>laufzeit</code> erneut
                verl�ngert werden? */
            public boolean verlaengern;
            
            public String toString()
            {
                StringBuffer ret=new StringBuffer();
                
                ret.append("Verl�ngerung: ");
                ret.append("Laufzeit ");
                ret.append(Integer.toString(laufzeit));
                ret.append(" Betrag ");
                ret.append(betrag.toString());
                ret.append(" weiter_verlaengern: ");
                ret.append(Boolean.toString(verlaengern));
                
                return ret.toString();
            }
        }
        
        /** Konto f�r die Festgeldanlage */
        public Konto                anlagekonto;
        /** Konto f�r Abbuchung der regelm��igen Betr�ge. */
        public Konto                belastungskonto;
        /** Konto, welchem der Anlagebetrag nach Ablauf der Festgeldanlage gutgeschrieben wird */
        public Konto                ausbuchungskonto;
        /** Konto, welchem die Zinsen f�r die Festgeldanlage gutgeschrieben werden */
        public Konto                zinskonto;
        /** Identifikationsnummer dieser Festgeldanlage f�r weitere Bearbeitung (optional) */
        public String               id;
        /** Angelegter Geldbetrag */
        public Value                anlagebetrag;
        /** Voraussichtlicher Zinsertrag dieser Anlage (optional) */
        public Value                zinsbetrag; 
        /** Konditionen, die f�r diese Festgeldanlage ausgehandelt wurden */
        public GVRFestCondList.Cond konditionen;
        /** Soll die Festgeldanlage nach Ablauf des Anlagezeitraumes verl�ngert werden? Wenn
            ja, dann enth�lt das Feld <code>verlaengerung</code> entsprechende Informationen dar�ber */
        public boolean              verlaengern;
        /** Format, in welchem der Kontoauszug f�r diese Anlage zugestellt wird.
            <ul>
              <li>0 = Wert nicht gesetzt</li>
              <li>1 = Zustellung per Post</li>
              <li>2 = Abholung (Kontoauszugsdrucker)</li>
            </ul> */
        public int                  kontoauszug;
        /** Status dieser Festgeldanlage.<ul>
            <li>0=Wert nicht gesetzt</li>
            <li>1=aktiv</li>
            <li>2=vorgemerkt</li></ul> */
        public int                  status;
        /** Informationen, wie im Falle einer Verl�ngerung verl�ngert werden soll (optional) */
        public Prolong              verlaengerung;
        
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String       linesep=System.getProperty("line.separator");
            
            if (anlagekonto!=null)
                ret.append("Anlagekonto: ").append(anlagekonto.toString()).append(linesep);
            ret.append("Belastungskonto: ").append(belastungskonto.toString()).append(linesep);
            ret.append("Ausbuchungskonto: ").append((ausbuchungskonto!=null?ausbuchungskonto.toString():belastungskonto.toString())).append(linesep);
            ret.append("Zinskonto: ").append((zinskonto!=null?zinskonto.toString():belastungskonto.toString())).append(linesep);
            ret.append("Anlagebetrag: ").append(anlagebetrag.toString()).append(linesep);
            if (zinsbetrag!=null)
                ret.append("Voraussichtlicher Zinsbetrag: ").append(zinsbetrag.toString()).append(linesep);
            ret.append("Nach Ablauf verl�ngern: ").append(Boolean.toString(verlaengern)).append(linesep);
            ret.append(konditionen.toString()+linesep);
            if (verlaengern)
                ret.append(linesep+verlaengerung.toString());
            
            return ret.toString().trim();
        }
    }
    
    private List<Entry> entries;
    
    public GVRFestList()
    {
        entries=new ArrayList<Entry>();
    }
    
    public void addEntry(Entry entry)
    {
        entries.add(entry);
    }
    
    /** Gibt Informationen �ber alle gefundenen Festgeldanlagen zur�ck
        @return Array, wobei jeder Eintrag eine Festgeldanlage beschreibt */
    public Entry[] getEntries()
    {
        return entries.toArray(new Entry[entries.size()]);
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        String       linesep=System.getProperty("line.separator");
        
        for (int i=0;i<entries.size();i++) {
            Entry entry= entries.get(i);
            
            ret.append("Festgeldanlage #").append(i).append(linesep);
            ret.append(entry.toString());
            ret.append(linesep+linesep);
        }
        
        return ret.toString().trim();
    }
}
