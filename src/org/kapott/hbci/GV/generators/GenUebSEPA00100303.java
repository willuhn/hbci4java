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
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.AccountIdentificationSEPA;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.ActiveOrHistoricCurrencyAndAmountSEPA;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.ActiveOrHistoricCurrencyCodeEUR;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.AmountTypeSEPA;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.BranchAndFinancialInstitutionIdentificationSEPA1;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.BranchAndFinancialInstitutionIdentificationSEPA3;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.CashAccountSEPA1;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.CashAccountSEPA2;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.ChargeBearerTypeSEPACode;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.CreditTransferTransactionInformationSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.CustomerCreditTransferInitiationV03;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.Document;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.FinancialInstitutionIdentificationSEPA1;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.FinancialInstitutionIdentificationSEPA3;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.GroupHeaderSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.ObjectFactory;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.PartyIdentificationSEPA1;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.PartyIdentificationSEPA2;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.PaymentIdentificationSEPA;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.PaymentInstructionInformationSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.PaymentMethodSCTCode;
import org.kapott.hbci.sepa.jaxb.pain_001_003_03.RemittanceInformationSEPA1Choice;

public class GenUebSEPA00100303 implements ISEPAGenerator{

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
		
		
		//Customer Credit Transfer Initiation
		doc.setCstmrCdtTrfInitn(new CustomerCreditTransferInitiationV03());
		doc.getCstmrCdtTrfInitn().setGrpHdr(new GroupHeaderSCT());
		
		
		//Group Header
		doc.getCstmrCdtTrfInitn().getGrpHdr().setMsgId(job.getSEPAParam("sepaid"));
		doc.getCstmrCdtTrfInitn().getGrpHdr().setCreDtTm(df.newXMLGregorianCalendar(sdtf.format(now)));
		doc.getCstmrCdtTrfInitn().getGrpHdr().setNbOfTxs("1");
		doc.getCstmrCdtTrfInitn().getGrpHdr().setInitgPty(new PartyIdentificationSEPA1());
		doc.getCstmrCdtTrfInitn().getGrpHdr().getInitgPty().setNm(job.getSEPAParam("src.name"));
		
		
		//Payment Information 
		ArrayList<PaymentInstructionInformationSCT> pmtInfs = (ArrayList<PaymentInstructionInformationSCT>) doc.getCstmrCdtTrfInitn().getPmtInf();
		PaymentInstructionInformationSCT pmtInf = new PaymentInstructionInformationSCT();
		pmtInfs.add(pmtInf);
		
		//FIXME: Wo kommt die ID her und wie muss sie aussehen?
		pmtInf.setPmtInfId(job.getSEPAParam("sepaid")); 
		pmtInf.setPmtMtd(PaymentMethodSCTCode.TRF);
		
		pmtInf.setReqdExctnDt(df.newXMLGregorianCalendar("1999-01-01"));
		pmtInf.setDbtr(new PartyIdentificationSEPA2());
		pmtInf.setDbtrAcct(new CashAccountSEPA1());
		pmtInf.setDbtrAgt(new BranchAndFinancialInstitutionIdentificationSEPA3());
		
		
		//Payment Information - Debtor
		pmtInf.getDbtr().setNm(job.getSEPAParam("src.name"));
		
		
		//Payment Information - DebtorAccount
		pmtInf.getDbtrAcct().setId(new AccountIdentificationSEPA());
		pmtInf.getDbtrAcct().getId().setIBAN(job.getSEPAParam("src.iban"));
		
		
		//Payment Information - DebtorAgent
		pmtInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSEPA3());
		pmtInf.getDbtrAgt().getFinInstnId().setBIC(job.getSEPAParam("src.bic"));
		
		
		//Payment Information - ChargeBearer
		pmtInf.setChrgBr(ChargeBearerTypeSEPACode.SLEV);
		
		
		//Payment Information - Credit Transfer Transaction Information
		ArrayList<CreditTransferTransactionInformationSCT> cdtTrxTxInfs = (ArrayList<CreditTransferTransactionInformationSCT>) pmtInf.getCdtTrfTxInf();
		CreditTransferTransactionInformationSCT cdtTrxTxInf = new CreditTransferTransactionInformationSCT();
		cdtTrxTxInfs.add(cdtTrxTxInf);
		
		
		//Payment Information - Credit Transfer Transaction Information - Payment Identification
		cdtTrxTxInf.setPmtId(new PaymentIdentificationSEPA());
		cdtTrxTxInf.getPmtId().setEndToEndId(job.getSEPAParam("endtoendid"));
		
		
		//Payment Information - Credit Transfer Transaction Information - Creditor
		cdtTrxTxInf.setCdtr(new PartyIdentificationSEPA2());
		cdtTrxTxInf.getCdtr().setNm(job.getSEPAParam("dst.name"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Account
		cdtTrxTxInf.setCdtrAcct(new CashAccountSEPA2());
		cdtTrxTxInf.getCdtrAcct().setId(new AccountIdentificationSEPA());
		cdtTrxTxInf.getCdtrAcct().getId().setIBAN(job.getSEPAParam("dst.iban"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Agent
		cdtTrxTxInf.setCdtrAgt(new BranchAndFinancialInstitutionIdentificationSEPA1());
		cdtTrxTxInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSEPA1());
		cdtTrxTxInf.getCdtrAgt().getFinInstnId().setBIC(job.getSEPAParam("dst.bic"));


		//Payment Information - Credit Transfer Transaction Information - Amount
		cdtTrxTxInf.setAmt(new AmountTypeSEPA());
		cdtTrxTxInf.getAmt().setInstdAmt(new ActiveOrHistoricCurrencyAndAmountSEPA());
		cdtTrxTxInf.getAmt().getInstdAmt().setValue(new BigDecimal(job.getSEPAParam("btg.value")));
		
		//FIXME: Schema sagt es gibt nur "eur" aber besser w�re bestimmt trotzdem getSEPAParam("btg.curr") oder?
		cdtTrxTxInf.getAmt().getInstdAmt().setCcy(ActiveOrHistoricCurrencyCodeEUR.EUR); 
		
		

		//Payment Information - Credit Transfer Transaction Information - Usage
		//FIXME: momentan nur unstrukturierter Verwendungszweck! Vielleicht gibt es einen Parameter daf�r? Dann kann man per If entscheiden
		cdtTrxTxInf.setRmtInf(new RemittanceInformationSEPA1Choice());
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
