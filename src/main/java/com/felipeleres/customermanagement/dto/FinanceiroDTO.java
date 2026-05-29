package com.felipeleres.customermanagement.dto;

import java.math.BigDecimal;

public class FinanceiroDTO {

    private BigDecimal totalPago;
    private BigDecimal totalEmAberto;
    private BigDecimal totalEmAtraso;

    public FinanceiroDTO(){

    }

    public FinanceiroDTO(BigDecimal totalPago, BigDecimal totalEmAberto, BigDecimal totalEmAtraso) {
        this.totalPago = totalPago;
        this.totalEmAberto = totalEmAberto;
        this.totalEmAtraso = totalEmAtraso;
    }

    public BigDecimal getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(BigDecimal totalPago) {
        this.totalPago = totalPago;
    }

    public BigDecimal getTotalEmAberto() {
        return totalEmAberto;
    }

    public void setTotalEmAberto(BigDecimal totalEmAberto) {
        this.totalEmAberto = totalEmAberto;
    }

    public BigDecimal getTotalEmAtraso() {
        return totalEmAtraso;
    }

    public void setTotalEmAtraso(BigDecimal totalEmAtraso) {
        this.totalEmAtraso = totalEmAtraso;
    }
}
