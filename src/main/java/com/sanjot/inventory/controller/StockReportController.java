package com.sanjot.inventory.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sanjot.inventory.entity.Transaction;
import com.sanjot.inventory.repository.DepartmentRepository;
import com.sanjot.inventory.repository.TransactionRepository;
import com.sanjot.inventory.utils.StockExportUtil;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/stock-report")
public class StockReportController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping
    public String viewStockReportPage(Model model,
                                      @RequestParam(required = false) Long departmentId) {

        List<Transaction> stockList;

        if (departmentId != null) {
            stockList = transactionRepository.findApprovedRemovedNotReturnedByDepartment(departmentId);
        } else {
            stockList = transactionRepository.findAllApprovedRemovedNotReturned();

        }

        model.addAttribute("stockList", stockList);
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("selectedDept", departmentId);
        return "stock-report";
    }
    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response,
                              @RequestParam(required = false) Long departmentId) throws IOException {
        List<Transaction> stockList = filterInventoryByDepartment(departmentId);
        StockExportUtil.exportToExcel(stockList, response);
    }

    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response,
                            @RequestParam(required = false) Long departmentId) throws IOException {
        List<Transaction> stockList = filterInventoryByDepartment(departmentId);
        StockExportUtil.exportToPdf(stockList, response);
    }

    private List<Transaction> filterInventoryByDepartment(Long departmentId) {
        if (departmentId != null) {
            return transactionRepository.findByDepartmentId(departmentId);
        } else {
            return transactionRepository.findAll();
        }
    }
}
