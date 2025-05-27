package com.example.agent_test_camp.loan_chat.tools;

import com.example.agent_test_camp.loan_chat.domain.Loan;
import com.example.agent_test_camp.loan_chat.dto.CustomerDetailsDTO;
import com.example.agent_test_camp.loan_chat.dto.LoanDetailsDTO;
import com.example.agent_test_camp.loan_chat.services.CustomerLoanService;
import org.springframework.stereotype.Service;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

@Service
public class LoanAssistantTools {

    private final CustomerLoanService customerLoanService;

    public LoanAssistantTools(CustomerLoanService customerLoanService) {
        this.customerLoanService = customerLoanService;
    }

    @Tool(description = "Retrieve loan details for a given user. Use this tool whenever a user asks for loan information.")
    public List<LoanDetailsDTO> getLoanDetails(
            @ToolParam(description = "Includes first name, last name.")
            CustomerDetailsDTO request) {
            return customerLoanService.getLoansByCustomerFistLastName(request);
    }
}
