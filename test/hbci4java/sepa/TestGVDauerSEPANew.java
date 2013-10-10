package hbci4java.sepa;

import org.junit.Test;
import hbci4java.AbstractTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.HBCIJobResult;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.status.HBCIExecStatus;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;


public class TestGVDauerSEPANew extends AbstractTest {
    private final static int LOGLEVEL = 5;
    private final static Map<Integer,String> settings = new HashMap<Integer,String>()
    {{
      // Demo-Konto bei der GAD
      put(HBCICallback.NEED_COUNTRY,         "DE");
      put(HBCICallback.NEED_FILTER,          "Base64");
      put(HBCICallback.NEED_PASSPHRASE_LOAD, "test");
      put(HBCICallback.NEED_PASSPHRASE_SAVE, "test");
      put(HBCICallback.NEED_PORT,            "443");
      put(HBCICallback.NEED_CONNECTION,      ""); // ignorieren
      put(HBCICallback.CLOSE_CONNECTION,     ""); // ignorieren
    }};
    
    private static File dir             = null;
    
    private HBCIPassportPinTan passport = null;
    private HBCIHandler handler         = null;
    private Properties params           = new Properties();
    
    
    @Test
    public void test() {
        System.out.println("---------Erstelle Job");
        HBCIJob job =  handler.newJob("DauerSEPANew");
        
        Konto acc = new Konto();
        acc.blz = params.getProperty("target_blz");
        acc.number = params.getProperty("target_number");
        acc.name = "Kurt Mustermann";
        acc.bic = params.getProperty("target_bic");
        acc.iban = params.getProperty("target_iban");
        
        
        job.setParam("src",passport.getAccounts()[0]);
        job.setParam("dst",acc);
        job.setParam("btg",new Value(100L,"EUR"));
        job.setParam("usage","SEPA Dauerauftrag");
        job.setParam("firstdate", "2014-01-01");
        job.setParam("timeunit", "M");
        job.setParam("turnus", "1");
        job.setParam("execday", "1");
        
        System.out.println("---------F�r Job zur Queue");
        job.addToQueue();

        
        HBCIExecStatus ret = handler.execute();
        HBCIJobResult res = job.getJobResult();
        System.out.println("----------Result: "+res.toString());
        
        Assert.assertEquals("Job Result ist nicht OK!", true, res.isOK());
    }

    /**
     * Erzeugt das Passport-Objekt.
     * @throws Exception
     */
    @Before
    public void beforeTest() throws Exception
    {
      // Testdatei im Arbeitsverzeichnis - sollte in der Run-Konfiguration auf ein eigenes Verzeichnis zeigen  
      String workDir = System.getProperty("user.dir");
      InputStream in = new FileInputStream(workDir+"/DauerSEPANew.properties");
      params.load(in);
      
      settings.put(HBCICallback.NEED_BLZ, params.getProperty("blz"));
      settings.put(HBCICallback.NEED_CUSTOMERID, params.getProperty("customerid"));
      settings.put(HBCICallback.NEED_HOST, params.getProperty("host"));
      settings.put(HBCICallback.NEED_PT_PIN, params.getProperty("pin"));
      settings.put(HBCICallback.NEED_USERID, params.getProperty("userid"));
      settings.put(HBCICallback.NEED_PT_SECMECH, params.getProperty("secmech"));
              
      Properties props = new Properties();
      props.put("log.loglevel.default",Integer.toString(LOGLEVEL));
      props.put("infoPoint.enabled",Boolean.FALSE.toString());
      
      props.put("client.passport.PinTan.filename",dir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".pt");
      props.put("client.passport.PinTan.init","1");
      props.put("client.passport.PinTan.checkcert","0"); // Check der SSL-Zertifikate abschalten - brauchen wir nicht fuer den Test
      
      // falls noetig
      props.put("client.passport.PinTan.proxy",""); // host:port
      props.put("client.passport.PinTan.proxyuser","");
      props.put("client.passport.PinTan.proxypass","");
      
      HBCICallback callback = new HBCICallbackConsole()
      {
        public void callback(HBCIPassport passport, int reason, String msg, int datatype, StringBuffer retData)
        {
          // haben wir einen vordefinierten Wert?
          String value = settings.get(reason);
          if (value != null)
          {
            retData.replace(0,retData.length(),value);
            return;
          }

          // Ne, dann an Super-Klasse delegieren
          super.callback(passport, reason, msg, datatype, retData);
        }
      };
      
      HBCIUtils.init(props,callback);
      this.passport = (HBCIPassportPinTan) AbstractHBCIPassport.getInstance("PinTan");
      
      // init handler
      this.handler = new HBCIHandler("300",passport);

      // dump bpd
      //this.dump("BPD",this.passport.getBPD());
      
      // Liste der unterstuetzten Geschaeftsvorfaelle ausgeben
      // this.dump("Supported GV",this.handler.getSupportedLowlevelJobs());
    }
    
    /**
     * Schliesst das Passport-Objekt und loescht die Passport-Datei.
     * @throws Exception
     */
    @After
    public void afterTest() throws Exception
    {
      try
      {
        if (this.passport != null)
          this.passport.close();
        
        File file = new File(this.passport.getFileName());
        if (!file.delete())
          throw new Exception("unable to delete " + file);
      }
      finally
      {
        try
        {
          if (this.handler != null)
            this.handler.close();
        }
        finally
        {
          HBCIUtils.done();
        }
      }
    }
    
    /**
     * Erzeugt das Passport-Verzeichnis.
     * @throws Exception
     */
    @BeforeClass
    public static void beforeClass() throws Exception
    {
      String tmpDir = System.getProperty("java.io.tmpdir","/tmp");
      dir = new File(tmpDir,"hbci4java-junit-" + System.currentTimeMillis());
      dir.mkdirs();
    }
    
    /**
     * Loescht das Passport-Verzeichnis.
     * @throws Exception
     */
    @AfterClass
    public static void afterClass() throws Exception
    {
      if (!dir.delete())
        throw new Exception("unable to delete " + dir);
    }
    
    private void dump(String name, Properties props)
    {
      System.out.println("--- BEGIN: " + name + " -----");
      Iterator keys = props.keySet().iterator();
      while (keys.hasNext())
      {
        Object key = keys.next();
        System.out.println(key + ": " + props.get(key));
      }
      System.out.println("--- END: " + name + " -----");
    }

}
