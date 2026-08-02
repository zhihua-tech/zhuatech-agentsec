/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.agentsec.model;
import jakarta.persistence.*;
@Entity @Table(name="agentsec_operating_unit") public class OperatingUnit extends BaseEntity {
    @Column(nullable=false,unique=true,length=32) private String code; @Column(nullable=false,length=80) private String name;
    @Column(nullable=false,length=60) private String workshop; @Column(nullable=false) private int plannedCapacity; @Column(nullable=false) private boolean active=true;
    protected OperatingUnit(){} public OperatingUnit(String code,String name,String workshop,int plannedCapacity){this.code=code;this.name=name;this.workshop=workshop;this.plannedCapacity=plannedCapacity;}
    public String getCode(){return code;} public String getName(){return name;} public String getWorkshop(){return workshop;} public int getPlannedCapacity(){return plannedCapacity;} public boolean isActive(){return active;}
}
