import { HttpInterceptorFn, HttpRequest } from '@angular/common/http';

/**
 * Adds X-Request-Name header to help identify requests in the Network tab.
 * Note: Chrome DevTools derives the Name column from the URL path; adding
 * descriptive path segments (e.g. /loans/overview/1 vs /loans/1) would require backend changes.
 */
export const httpRequestNameInterceptor: HttpInterceptorFn = (req, next) => {
  const name = getRequestName(req);
  if (name) {
    req = req.clone({ setHeaders: { 'X-Request-Name': name } });
  }
  return next(req);
};

function getRequestName(req: HttpRequest<unknown>): string {
  const url = req.url;
  if (url.includes('getOverviewDeatils') || url.includes('getOverviewDetails')) return 'overview';
  if (url.includes('getProductDetails') || url.includes('saveProductDetails')) return 'business-product';
  if (url.includes('getCompanyDetails') || url.includes('saveCompanyDetails')) return 'company-details';
  if (url.includes('getCompanyAddress') || url.includes('saveCompanyAddress')) return 'company-address';
  if (url.includes('saveJsonfileData')) return 'assurance-details';
  if (url.includes('filtertransactions')) return 'txn-filters';
  if (url.includes('fetchtransactions')) return 'txn-statement';
  if (url.includes('loanTaskboard')) return 'loan-taskboard';
  if (url.includes('applyLoan')) return 'apply-loan';
  if (url.includes('getAll')) return 'employees';
  if (url.includes('readExcel') || url.includes('saveSalesReport') || url.includes('getSalesReportDetails')) return 'sales-report';
  if (url.includes('readTransactionsCsv') || url.includes('saveTxnsData')) return 'transactions-csv';
  if (url.includes('readJson') || url.includes('getPersonDetails')) return 'assurance-details';
  return '';
}
