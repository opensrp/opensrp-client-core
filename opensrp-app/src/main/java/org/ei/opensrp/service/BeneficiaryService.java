package org.ei.opensrp.service;

import org.ei.opensrp.domain.Child;
import org.ei.opensrp.domain.EligibleCouple;
import org.ei.opensrp.domain.Mother;
import org.ei.opensrp.repository.AllBeneficiaries;
import org.ei.opensrp.repository.AllEligibleCouples;
import org.ei.opensrp.view.contract.Beneficiary;

import java.util.ArrayList;
import java.util.List;

public class BeneficiaryService {
    private final AllEligibleCouples allEligibleCouples;
    private final AllBeneficiaries allBeneficiaries;

    public BeneficiaryService(AllEligibleCouples allEligibleCouples, AllBeneficiaries allBeneficiaries) {
        this.allEligibleCouples = allEligibleCouples;
        this.allBeneficiaries = allBeneficiaries;
    }

    public List<Beneficiary> fetchFromEcCaseIds(List<String> caseIds) {
        List<Beneficiary> beneficiaries = new ArrayList<Beneficiary>();
        List<EligibleCouple> eligibleCouples = allEligibleCouples.findByCaseIDs(caseIds);
        for (EligibleCouple ec : eligibleCouples) {
            beneficiaries.add(new Beneficiary(ec.caseId(), ec.wifeName(), ec.husbandName(), "", ec.ecNumber(), ec.village(), ec.isHighPriority()));
        }
        return beneficiaries;
    }

    public List<Beneficiary> fetchFromChildCaseIds(List<String> caseIds) {
        List<Beneficiary> beneficiaries = new ArrayList<Beneficiary>();
        List<Child> children = allBeneficiaries.findAllChildrenByCaseIDs(caseIds);
        for (Child child : children) {
            Mother mother = allBeneficiaries.findMother(child.motherCaseId());
            EligibleCouple parents = allEligibleCouples.findByCaseID(mother.ecCaseId());
            beneficiaries.add(new Beneficiary(child.caseId(), parents.wifeName(), parents.husbandName(), child.thayiCardNumber(), parents.ecNumber(), parents.village(), child.isHighRisk()));
        }
        return beneficiaries;
    }

    public List<Beneficiary> fetchFromMotherCaseIds(List<String> caseIds) {
        List<Beneficiary> beneficiaries = new ArrayList<Beneficiary>();
        List<Mother> mothers = allBeneficiaries.findAllMothersByCaseIDs(caseIds);
        for (Mother mother : mothers) {
            EligibleCouple ec = allEligibleCouples.findByCaseID(mother.ecCaseId());
            beneficiaries.add(new Beneficiary(mother.caseId(), ec.wifeName(), ec.husbandName(), mother.thayiCardNumber(), ec.ecNumber(), ec.village(), mother.isHighRisk()));
        }
        return beneficiaries;
    }
}
