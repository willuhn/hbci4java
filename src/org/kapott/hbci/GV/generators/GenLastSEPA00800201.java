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
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.AccountIdentificationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.AmendmentInformationDetailsSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.BranchAndFinancialInstitutionIdentificationSDD1;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.BranchAndFinancialInstitutionIdentificationSDD2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.CashAccountSDD1;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.CashAccountSDD2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.ChargeBearerTypeSDDCode;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.CurrencyAndAmountSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.CurrencyCodeSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.DirectDebitTransactionInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.DirectDebitTransactionSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.Document;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.FinancialInstitutionIdentificationSDD1;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.FinancialInstitutionIdentificationSDD2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.GenericIdentificationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.GroupHeaderSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.MandateRelatedInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.ObjectFactory;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.Pain00800101;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PartyIdentificationSDD1;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PartyIdentificationSDD2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PartyIdentificationSDD3;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PartyIdentificationSDD4;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PartySDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PaymentIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PaymentInstructionInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PaymentMethod2Code;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PersonIdentificationSDD2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.RemittanceInformationSDDChoice;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.RestrictedIdentificationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.RestrictedSEPACode;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.RestrictedSMNDACode;


public class GenLastSEPA00800201 implements ISEPAGenerator{

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
		doc.setPain00800101(new Pain00800101());
		doc.getPain00800101().setGrpHdr(new GroupHeaderSDD());
		
		
		//Group Header
		doc.getPain00800101().getGrpHdr().setMsgId(job.getSEPAParam("sepaid"));
		doc.getPain00800101().getGrpHdr().setCreDtTm(df.newXMLGregorianCalendar(sdtf.format(now)));
		doc.getPain00800101().getGrpHdr().setNbOfTxs("1");
		doc.getPain00800101().getGrpHdr().setInitgPty(new PartyIdentificationSDD1());
		doc.getPain00800101().getGrpHdr().getInitgPty().setNm(job.getSEPAParam("src.name"));
		
		
		//Payment Information 
		ArrayList<PaymentInstructionInformationSDD> pmtInfs = (ArrayList<PaymentInstructionInformationSDD>) doc.getPain00800101().getPmtInf();
		PaymentInstructionInformationSDD pmtInf = new PaymentInstructionInformationSDD();
		pmtInfs.add(pmtInf);
		
		pmtInf.setPmtInfId(job.getSEPAParam("sepaid")); 
		pmtInf.setPmtMtd(PaymentMethod2Code.DD);
		
		pmtInf.setReqdColltnDt(df.newXMLGregorianCalendar("1999-01-01"));
		pmtInf.setCdtr(new PartyIdentificationSDD2());
		pmtInf.setCdtrAcct(new CashAccountSDD1());
		pmtInf.setCdtrAgt(new BranchAndFinancialInstitutionIdentificationSDD1());
				
		//Payment Information
		pmtInf.getCdtr().setNm(job.getSEPAParam("src.name"));
				
		//Payment Information
		pmtInf.getCdtrAcct().setId(new AccountIdentificationSDD());
		pmtInf.getCdtrAcct().getId().setIBAN(job.getSEPAParam("src.iban"));
				
		//Payment Information
		pmtInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSDD1());
		pmtInf.getCdtrAgt().getFinInstnId().setBIC(job.getSEPAParam("src.bic"));
		
		
		//Payment Information - ChargeBearer
		pmtInf.setChrgBr(ChargeBearerTypeSDDCode.SLEV);
		
		
		//Payment Information - Credit Transfer Transaction Information
		ArrayList<DirectDebitTransactionInformationSDD> drctDbtTxInfs = (ArrayList<DirectDebitTransactionInformationSDD>) pmtInf.getDrctDbtTxInf();
		DirectDebitTransactionInformationSDD drctDbtTxInf = new DirectDebitTransactionInformationSDD();
		drctDbtTxInfs.add(drctDbtTxInf);
		
		
		
		
		//FIXME: SEPA Mandant
		drctDbtTxInf.setDrctDbtTx(new DirectDebitTransactionSDD());
		drctDbtTxInf.getDrctDbtTx().setCdtrSchmeId(new PartyIdentificationSDD4()); 
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().setId(new PartySDD());
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().setPrvtId(new PersonIdentificationSDD2());
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().setOthrId(new GenericIdentificationSDD());
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthrId().setId(job.getSEPAParam("src.iban"));
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthrId().setIdTp(RestrictedSEPACode.SEPA);

				
		drctDbtTxInf.getDrctDbtTx().setMndtRltdInf(new MandateRelatedInformationSDD());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setMndtId(job.getSEPAParam("mandateid"));
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setDtOfSgntr(df.newXMLGregorianCalendar(job.getSEPAParam("manddateofsig"))); //FIXME: Wird das datum richtig geparst?
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setAmdmntInd(Boolean.valueOf(job.getSEPAParam("amendmandindic")));
		//FIXME: Ich glaube die AmdmntInfDtls wird nur gebraucht wenn AmdmntInd true ist. Wenn ja muss das hier in ne if
//		if(Boolean.valueOf(job.getSEPAParam("amendmandindic")) == true){
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setAmdmntInfDtls(new AmendmentInformationDetailsSDD());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().setOrgnlDbtrAgt(new BranchAndFinancialInstitutionIdentificationSDD2());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSDD2());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().getFinInstnId().setPrtryId(new RestrictedIdentificationSDD());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().getFinInstnId().getPrtryId().setId(RestrictedSMNDACode.SMNDA);
//		}		
		
		
		
		//Payment Information - Credit Transfer Transaction Information - Payment Identification
		drctDbtTxInf.setPmtId(new PaymentIdentification1());
		drctDbtTxInf.getPmtId().setEndToEndId(job.getSEPAParam("endtoendid"));
		
		
		//Payment Information - Credit Transfer Transaction Information - Creditor
		drctDbtTxInf.setDbtr(new PartyIdentificationSDD3());
		drctDbtTxInf.getDbtr().setNm(job.getSEPAParam("dst.name"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Account
		drctDbtTxInf.setDbtrAcct(new CashAccountSDD2());
		drctDbtTxInf.getDbtrAcct().setId(new AccountIdentificationSDD());
		drctDbtTxInf.getDbtrAcct().getId().setIBAN(job.getSEPAParam("dst.iban"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Agent
		drctDbtTxInf.setDbtrAgt(new BranchAndFinancialInstitutionIdentificationSDD1());
		drctDbtTxInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSDD1());
		drctDbtTxInf.getDbtrAgt().getFinInstnId().setBIC(job.getSEPAParam("dst.bic"));


		//Payment Information - Credit Transfer Transaction Information - Amount
		drctDbtTxInf.setInstdAmt(new CurrencyAndAmountSDD());
		drctDbtTxInf.getInstdAmt().setValue(new BigDecimal(job.getSEPAParam("btg.value")));
		
		//FIXME: Schema sagt es gibt nur "eur" aber besser w�re bestimmt trotzdem getSEPAParam("btg.curr") oder?
		drctDbtTxInf.getInstdAmt().setCcy(CurrencyCodeSDD.EUR); 
		

		//Payment Information - Credit Transfer Transaction Information - Usage
		//FIXME: momentan nur unstrukturierter Verwendungszweck! Vielleicht gibt es einen Parameter daf�r? Dann kann man per If entscheiden
		drctDbtTxInf.setRmtInf(new RemittanceInformationSDDChoice());
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
