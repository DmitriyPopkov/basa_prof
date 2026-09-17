package com.example.basa_prof.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Реализация фасада для агрегации всех сервисов приложения.
 */
@Component
public class ApplicationServiceFacade implements IApplicationServiceFacade {

    @Autowired
    private ClientService clientService;
    @Autowired
    private DealService dealService;
    @Autowired
    private ObjectEntityService objectEntityService;
    @Autowired
    private WorkService workService;
    @Autowired
    private MaterialService materialService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private UserService userService;
    @Autowired
    private WorkHourService workHourService;
    @Autowired
    private ContractorService contractorService;
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private ReportService reportService;

    @Override
    public IClientService getClientService() {
        return clientService;
    }

    @Override
    public IDealService getDealService() {
        return dealService;
    }

    @Override
    public IObjectEntityService getObjectEntityService() {
        return objectEntityService;
    }

    @Override
    public IWorkService getWorkService() {
        return workService;
    }

    @Override
    public IMaterialService getMaterialService() {
        return materialService;
    }

    @Override
    public IEmployeeService getEmployeeService() {
        return employeeService;
    }

    @Override
    public ISupplierService getSupplierService() {
        return supplierService;
    }

    @Override
    public IUserService getUserService() {
        return userService;
    }

    @Override
    public IWorkHourService getWorkHourService() {
        return workHourService;
    }

    @Override
    public IContractorService getContractorService() {
        return contractorService;
    }

    @Override
    public IExpenseService getExpenseService() {
        return expenseService;
    }

    @Override
    public ReportService getReportService() {
        return reportService;
    }
}
