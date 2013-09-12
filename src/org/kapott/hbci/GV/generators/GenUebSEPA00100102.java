package org.kapott.hbci.GV.generators;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;

import org.kapott.hbci.GV.GVUebSEPA;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.AccountIdentification2;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.AmountType3;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.CashAccount8;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.ChargeBearerType2Code;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.CreditTransferTransactionInformation2;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.Document;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.EuroMax9Amount;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.FinancialInstitution2;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.FinancialInstitutionIdentification4;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.GroupHeader20;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.ObjectFactory;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.Pain00100102;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PartyIdentification20;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PartyIdentification21;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PartyIdentification23;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PaymentIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PaymentInstructionInformation4;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PaymentMethod5Code;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.RemittanceInformation3;


public class GenUebSEPA00100102 implements ISEPAGenerator{

	@Override
	public void generate(HBCIJob job, ByteArrayOutputStream os)
			throws Exception {
		
		
		generate((GVUebSEPA)job, os);
		
	}
	public void generate(GVUebSEPA job, ByteArrayOutputStream os) throws Exception {
		
		//Formatter um Dates ins gew�nschte ISODateTime Format zu bringen.
		Date now=new Date();
		SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	    DatatypeFactory df = DatatypeFactory.newInstance();
		
		
		//Document
		Document doc = new Document();
		
		
		//Pain00100102
		doc.setPain00100102(new Pain00100102());
		
		
		doc.getPain00100102().setGrpHdr(new GroupHeader20());
				
		//Group Header
		doc.getPain00100102().getGrpHdr().setMsgId(job.getSEPAParam("sepaid"));
		doc.getPain00100102().getGrpHdr().setCreDtTm(df.newXMLGregorianCalendar(sdtf.format(now)));
	    doc.getPain00100102().getGrpHdr().setNbOfTxs("1");
		doc.getPain00100102().getGrpHdr().setInitgPty(new PartyIdentification20());
		doc.getPain00100102().getGrpHdr().getInitgPty().setNm(job.getSEPAParam("src.name"));
		
		
		//Payment Information 
		PaymentInstructionInformation4 pmtInf = doc.getPain00100102().getPmtInf();
		
		//FIXME: Wo kommt die ID her und wie muss sie aussehen?
		pmtInf.setPmtInfId(job.getSEPAParam("sepaid")); 
		pmtInf.setPmtMtd(PaymentMethod5Code.TRF);
		
		pmtInf.setReqdExctnDt(df.newXMLGregorianCalendar("1999-01-01"));
		pmtInf.setDbtr(new PartyIdentification23());
		pmtInf.setDbtrAcct(new CashAccount8());
		pmtInf.setDbtrAgt(new FinancialInstitution2());
		
		
		//Payment Information - Debtor
		pmtInf.getDbtr().setNm(job.getSEPAParam("src.name"));
		
		
		//Payment Information - DebtorAccount
		pmtInf.getDbtrAcct().setId(new AccountIdentification2());
		pmtInf.getDbtrAcct().getId().setIBAN(job.getSEPAParam("src.iban"));
		
		
		//Payment Information - DebtorAgent
		pmtInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentification4());
		pmtInf.getDbtrAgt().getFinInstnId().setBIC(job.getSEPAParam("src.bic"));
		
		
		//Payment Information - ChargeBearer
		pmtInf.setChrgBr(ChargeBearerType2Code.SLEV);
		
		
		//Payment Information - Credit Transfer Transaction Information
		ArrayList<CreditTransferTransactionInformation2> cdtTrxTxInfs = (ArrayList<CreditTransferTransactionInformation2>) pmtInf.getCdtTrfTxInf();
		CreditTransferTransactionInformation2 cdtTrxTxInf = new CreditTransferTransactionInformation2();
		cdtTrxTxInfs.add(cdtTrxTxInf);
		
		
		//Payment Information - Credit Transfer Transaction Information - Payment Identification
		cdtTrxTxInf.setPmtId(new PaymentIdentification1());
		cdtTrxTxInf.getPmtId().setEndToEndId(job.getSEPAParam("endtoendid"));
		
		
		//Payment Information - Credit Transfer Transaction Information - Creditor
		cdtTrxTxInf.setCdtr(new PartyIdentification21());
		cdtTrxTxInf.getCdtr().setNm(job.getSEPAParam("dst.name"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Account
		cdtTrxTxInf.setCdtrAcct(new CashAccount8());
		cdtTrxTxInf.getCdtrAcct().setId(new AccountIdentification2());
		cdtTrxTxInf.getCdtrAcct().getId().setIBAN(job.getSEPAParam("dst.iban"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Agent
		cdtTrxTxInf.setCdtrAgt(new FinancialInstitution2());
		cdtTrxTxInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentification4());
		cdtTrxTxInf.getCdtrAgt().getFinInstnId().setBIC(job.getSEPAParam("dst.bic"));


		//Payment Information - Credit Transfer Transaction Information - Amount
		cdtTrxTxInf.setAmt(new AmountType3());
		cdtTrxTxInf.getAmt().setInstdAmt(new EuroMax9Amount());
		cdtTrxTxInf.getAmt().getInstdAmt().setValue(new BigDecimal(job.getSEPAParam("btg.value")));
		
		//FIXME: Schema sagt es gibt nur "eur" aber besser w�re bestimmt trotzdem getSEPAParam("btg.curr") oder?
		cdtTrxTxInf.getAmt().getInstdAmt().setCcy("EUR"); 
		
		

		//Payment Information - Credit Transfer Transaction Information - Usage
		//FIXME: momentan nur unstrukturierter Verwendungszweck! Vielleicht gibt es einen Parameter daf�r? Dann kann man per If entscheiden
		cdtTrxTxInf.setRmtInf(new RemittanceInformation3());
		cdtTrxTxInf.getRmtInf().setUstrd(job.getSEPAParam("usage"));


		writeDocToOutputStream(doc, os);
	}

	private void writeDocToOutputStream(Document doc, ByteArrayOutputStream os) throws Exception{
		//Fertiges Dokument mittels JAXB marshallen (XML in den ByteArrayOutputStream schreiben)
		ObjectFactory of = new ObjectFactory();		
		JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(of.createDocument(doc), os);
	}

}
