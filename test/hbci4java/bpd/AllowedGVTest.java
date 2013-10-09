package hbci4java.bpd;

import org.junit.Test;
import hbci4java.AbstractTest;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.factory.MSGFactory;
import org.kapott.hbci.rewrite.Rewrite;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.lang.reflect.Constructor;

public class AllowedGVTest extends AbstractTest {

	@Test
	public void test() throws Exception {
	    String data = getFile("bpd/bpd-allowedgv.txt");
	    HBCIKernelImpl kernel = new HBCIKernelImpl(null,"plus");
	    
	    Rewrite.setData("msgName","Synch");
        // liste der rewriter erzeugen
        String rewriters_st=HBCIUtils.getParam("kernel.rewriter");
        ArrayList al=new ArrayList();
        StringTokenizer tok=new StringTokenizer(rewriters_st,",");
        while (tok.hasMoreTokens()) {
            String rewriterName=tok.nextToken().trim();
            if (rewriterName.length()!=0) {
                Class cl=this.getClass().getClassLoader().loadClass("org.kapott.hbci.rewrite.R"+
                                                                    rewriterName);
                Constructor con=cl.getConstructor((Class[])null);
                Rewrite rewriter=(Rewrite)(con.newInstance((Object[])null));
                al.add(rewriter);
            }
        }
        Rewrite[] rewriters=(Rewrite[])al.toArray(new Rewrite[al.size()]);

	    kernel.rawNewMsg("Synch");
	    
	    MsgGen gen = kernel.getMsgGen();
	    
        // alle patches f�r die unverschl�sselte nachricht durchlaufen
	    String newmsgstring = data;
        for (int i=0;i<rewriters.length;i++) {
            newmsgstring=rewriters[i].incomingClearText(newmsgstring,gen);
        }

	    MSG msg = MSGFactory.getInstance().createMSG("SynchRes",newmsgstring,newmsgstring.length(),gen);
	    Hashtable<String,String> ht = new Hashtable<String,String>();
	    msg.extractValues(ht);	
	}
}
