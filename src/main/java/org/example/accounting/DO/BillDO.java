package org.example.accounting.DO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.example.accounting.model.Bill;
import org.example.accounting.model.User;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillDO {
    private Integer id;
    private String billfication;
    private String billtype;
    private Double amount;
    private String remark;
    private String username;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime addTime;

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBillfication() {
        return billfication;
    }

    public void setBillfication(String billfication) {
        this.billfication = billfication;
    }

    public String getBilltype() {
        return billtype;
    }

    public void setBilltype(String billtype) {
        this.billtype = billtype;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Bill toModel(BillDO billDO){
        Bill bill = new Bill();
        bill.setAmount(billDO.getAmount());
        bill.setBillfication(billDO.getBillfication());
        bill.setBilltype(billDO.getBilltype());
        bill.setRemark(billDO.getRemark());
        bill.setUsername(billDO.getUsername());
        bill.setBillTime(billDO.getAddTime());
        return bill;
    }
}
