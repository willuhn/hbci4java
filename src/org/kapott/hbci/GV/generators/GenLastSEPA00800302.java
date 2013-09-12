package org.kapott.hbci.GV.generators;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;

import org.kapott.hbci.GV.GVLastSEPA;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.AccountIdentificationSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.ActiveOrHistoricCurrencyAndAmountSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.ActiveOrHistoricCurrencyCodeEUR;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.AmendmentInformationDetailsSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.BranchAndFinancialInstitutionIdentificationSEPA2;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.BranchAndFinancialInstitutionIdentificationSEPA3;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.CashAccountSEPA1;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.CashAccountSEPA2;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.ChargeBearerTypeSEPACode;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.CustomerDirectDebitInitiationV02;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.DirectDebitTransactionInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.DirectDebitTransactionSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.Document;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.FinancialInstitutionIdentificationSEPA2;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.FinancialInstitutionIdentificationSEPA3;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.GroupHeaderSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.IdentificationSchemeNameSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.MandateRelatedInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.ObjectFactory;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.PartyIdentificationSEPA1;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.PartyIdentificationSEPA2;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.PartyIdentificationSEPA3;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.PartyIdentificationSEPA5;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.PartySEPA2;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.PaymentIdentificationSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.PaymentInstructionInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.PaymentMethod2Code;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.PersonIdentificationSEPA2;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.RemittanceInformationSEPA1Choice;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.RestrictedFinancialIdentificationSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.RestrictedPersonIdentificationSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.RestrictedPersonIdentificationSchemeNameSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.RestrictedSMNDACode;


public class GenLastSEPA00800302 implements ISEPAGenerator{

	@Override
	public void generate(HBCIJob job, ByteArrayOutputStream os)
			throws Exception {
		
		
		generate((GVLastSEPA)job, os);
		
	}
	public void generate(GVLastSEPA job, ByteArrayOutputStream os) throws Exception {
		
		//Formatter um Dates ins gew�nschte ISODateTime Format zu bringen.
		Date now=new Date();
		SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		DatatypeFactory df = DatatypeFactory.newInstance();
		
		
		//Document
		Document doc = new Document();
		
		
		//Customer Credit Transfer Initiation
		doc.setCstmrDrctDbtInitn(new CustomerDirectDebitInitiationV02());
		doc.getCstmrDrctDbtInitn().setGrpHdr(new GroupHeaderSDD());
		
		
		//Group Header
		doc.getCstmrDrctDbtInitn().getGrpHdr().setMsgId(job.getSEPAParam("sepaid"));
		doc.getCstmrDrctDbtInitn().getGrpHdr().setCreDtTm(df.newXMLGregorianCalendar(sdtf.format(now)));
		doc.getCstmrDrctDbtInitn().getGrpHdr().setNbOfTxs("1");
		doc.getCstmrDrctDbtInitn().getGrpHdr().setInitgPty(new PartyIdentificationSEPA1());
		doc.getCstmrDrctDbtInitn().getGrpHdr().getInitgPty().setNm(job.getSEPAParam("src.name"));
		
		
		//Payment Information 
		ArrayList<PaymentInstructionInformationSDD> pmtInfs = (ArrayList<PaymentInstructionInformationSDD>) doc.getCstmrDrctDbtInitn().getPmtInf();
		PaymentInstructionInformationSDD pmtInf = new PaymentInstructionInformationSDD();
		pmtInfs.add(pmtInf);
		
		pmtInf.setPmtInfId(job.getSEPAParam("sepaid")); 
		pmtInf.setPmtMtd(PaymentMethod2Code.DD);
		
		pmtInf.setReqdColltnDt(df.newXMLGregorianCalendar("1999-01-01"));
		pmtInf.setCdtr(new PartyIdentificationSEPA5());
		pmtInf.setCdtrAcct(new CashAccountSEPA1());
		pmtInf.setCdtrAgt(new BranchAndFinancialInstitutionIdentificationSEPA3());
				
		//Payment Information
		pmtInf.getCdtr().setNm(job.getSEPAParam("src.name"));
				
		//Payment Information
		pmtInf.getCdtrAcct().setId(new AccountIdentificationSEPA());
		pmtInf.getCdtrAcct().getId().setIBAN(job.getSEPAParam("src.iban"));
				
		//Payment Information
		pmtInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSEPA3());
		pmtInf.getCdtrAgt().getFinInstnId().setBIC(job.getSEPAParam("src.bic"));
		
		
		//Payment Information - ChargeBearer
		pmtInf.setChrgBr(ChargeBearerTypeSEPACode.SLEV);
		
		
		//Payment Information - Credit Transfer Transaction Information
		ArrayList<DirectDebitTransactionInformationSDD> drctDbtTxInfs = (ArrayList<DirectDebitTransactionInformationSDD>) pmtInf.getDrctDbtTxInf();
		DirectDebitTransactionInformationSDD drctDbtTxInf = new DirectDebitTransactionInformationSDD();
		drctDbtTxInfs.add(drctDbtTxInf);
		
		
		
		//FIXME: SEPA Mandant
		drctDbtTxInf.setDrctDbtTx(new DirectDebitTransactionSDD());
		drctDbtTxInf.getDrctDbtTx().setCdtrSchmeId(new PartyIdentificationSEPA3()); 
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().setId(new PartySEPA2());
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().setPrvtId(new PersonIdentificationSEPA2());
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().setOthr(new RestrictedPersonIdentificationSEPA());
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthr().setId(job.getSEPAParam("src.iban"));
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthr().setSchmeNm(new RestrictedPersonIdentificationSchemeNameSEPA());
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthr().getSchmeNm().setPrtry(IdentificationSchemeNameSEPA.SEPA);
		
		drctDbtTxInf.getDrctDbtTx().setMndtRltdInf(new MandateRelatedInformationSDD());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setMndtId(job.getSEPAParam("mandateid"));
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setDtOfSgntr(df.newXMLGregorianCalendar(job.getSEPAParam("manddateofsig"))); //FIXME: Wird das datum richtig geparst?
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setAmdmntInd(Boolean.valueOf(job.getSEPAParam("amendmandindic")));
		//FIXME: Ich glaube die AmdmntInfDtls wird nur gebraucht wenn AmdmntInd true ist. Wenn ja muss das hier in ne if
//		if(Boolean.valueOf(job.getSEPAParam("amendmandindic")) == true){
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setAmdmntInfDtls(new AmendmentInformationDetailsSDD());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().setOrgnlDbtrAgt(new BranchAndFinancialInstitutionIdentificationSEPA2());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSEPA2());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().getFinInstnId().setOthr(new RestrictedFinancialIdentificationSEPA());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().getFinInstnId().getOthr().setId(RestrictedSMNDACode.SMNDA);
//		}
		
		
		
		
		//Payment Information - Credit Transfer Transaction Information - Payment Identification
		drctDbtTxInf.setPmtId(new PaymentIdentificationSEPA());
		drctDbtTxInf.getPmtId().setEndToEndId(job.getSEPAParam("endtoendid"));
		
		
		//Payment Information - Credit Transfer Transaction Information - Creditor
		drctDbtTxInf.setDbtr(new PartyIdentificationSEPA2());
		drctDbtTxInf.getDbtr().setNm(job.getSEPAParam("dst.name"));
		
		
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Account
		drctDbtTxInf.setDbtrAcct(new CashAccountSEPA2());
		drctDbtTxInf.getDbtrAcct().setId(new AccountIdentificationSEPA());
		drctDbtTxInf.getDbtrAcct().getId().setIBAN(job.getSEPAParam("dst.iban"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Agent
		drctDbtTxInf.setDbtrAgt(new BranchAndFinancialInstitutionIdentificationSEPA3());
		drctDbtTxInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSEPA3());
		drctDbtTxInf.getDbtrAgt().getFinInstnId().setBIC(job.getSEPAParam("dst.bic"));


		//Payment Information - Credit Transfer Transaction Information - Amount
		drctDbtTxInf.setInstdAmt(new ActiveOrHistoricCurrencyAndAmountSEPA());
		drctDbtTxInf.getInstdAmt().setValue(new BigDecimal(job.getSEPAParam("btg.value")));
		
		//FIXME: Schema sagt es gibt nur "eur" aber besser w�re bestimmt trotzdem getSEPAParam("btg.curr") oder?
		drctDbtTxInf.getInstdAmt().setCcy(ActiveOrHistoricCurrencyCodeEUR.EUR); 
		

		//Payment Information - Credit Transfer Transaction Information - Usage
		//FIXME: momentan nur unstrukturierter Verwendungszweck! Vielleicht gibt es einen Parameter daf�r? Dann kann man per If entscheiden
		drctDbtTxInf.setRmtInf(new RemittanceInformationSEPA1Choice());
		drctDbtTxInf.getRmtInf().setUstrd(job.getSEPAParam("usage"));


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
