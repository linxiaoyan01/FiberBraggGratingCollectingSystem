package top.kaluna.modbusTcp.domain;

import java.math.BigDecimal;

public class FbgValueInfo {
    private Long id;

    private String propertyName;

    private BigDecimal min;

    private BigDecimal max;

    private Long distance;

    private Long createTime;

    private Integer category;

    private Integer channel;

    private Integer arrayNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Integer getArrayNum() {
        return arrayNum;
    }

    public void setArrayNum(Integer arrayNum) {
        this.arrayNum = arrayNum;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", propertyName=").append(propertyName);
        sb.append(", min=").append(min);
        sb.append(", max=").append(max);
        sb.append(", distance=").append(distance);
        sb.append(", createTime=").append(createTime);
        sb.append(", category=").append(category);
        sb.append(", channel=").append(channel);
        sb.append(", arrayNum=").append(arrayNum);
        sb.append("]");
        return sb.toString();
    }
}