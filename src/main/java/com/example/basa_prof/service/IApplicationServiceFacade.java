package com.example.basa_prof.service;

/**
 * Фасад для агрегации всех сервисов приложения.
 * Реализует принцип Interface Segregation (I) — UI зависит от одного интерфейса, а не от 10 сервисов.
 */
public interface IApplicationServiceFacade {
    IClientService getClientService();
    IDealService getDealService();
    IObjectEntityService getObjectEntityService();
    IWorkService getWorkService();
    IMaterialService getMaterialService();
    IEmployeeService getEmployeeService();
    ISupplierService getSupplierService();
    IUserService getUserService();
    IWorkHourService getWorkHourService();
    IContractorService getContractorService();
    IExpenseService getExpenseService();
    ReportService getReportService();
}
