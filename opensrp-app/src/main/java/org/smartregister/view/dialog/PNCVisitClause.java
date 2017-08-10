package org.smartregister.view.dialog;

import org.joda.time.LocalDate;
import org.smartregister.view.contract.ServiceProvidedDTO;

public class PNCVisitClause implements FilterClause<ServiceProvidedDTO> {
    private LocalDate visitEndDate;
    private String PNC_IDENTIFIER = "PNC";
//    private String PNC_IDENTIFIER = CoreLibrary.getInstance().context().getStringResource(R.string
// .str_pnc_clause);

    public PNCVisitClause(LocalDate visitEndDate) {
        this.visitEndDate = visitEndDate;
    }

    @Override
    public boolean filter(ServiceProvidedDTO service) {
        return PNC_IDENTIFIER.equalsIgnoreCase(service.name()) && service.localDate()
                .isBefore(visitEndDate);
    }
}
