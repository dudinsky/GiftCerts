package org.dudinskiy.giftcert.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class GiftCert {

    private Long giftCertId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;
    private List<Tag> tags;

    public Long getGiftCertId() {
        return giftCertId;
    }

    public void setGiftCertId(Long giftCertId) {
        this.giftCertId = giftCertId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GiftCert giftCert = (GiftCert) o;
        return Objects.equals(getGiftCertId(), giftCert.getGiftCertId()) && Objects.equals(getName(), giftCert.getName()) && Objects.equals(getDescription(), giftCert.getDescription()) && Objects.equals(getPrice(), giftCert.getPrice()) && Objects.equals(getDuration(), giftCert.getDuration()) && Objects.equals(getCreateDate(), giftCert.getCreateDate()) && Objects.equals(getLastUpdateDate(), giftCert.getLastUpdateDate()) && Objects.equals(getTags(), giftCert.getTags());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGiftCertId(), getName(), getDescription(), getPrice(), getDuration(), getCreateDate(), getLastUpdateDate(), getTags());
    }

    @Override
    public String toString() {
        return "GiftCert{" +
                "giftCertId=" + giftCertId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", duration=" + duration +
                ", createDate=" + createDate +
                ", lastUpdateDate=" + lastUpdateDate +
                ", tags=" + tags +
                '}';
    }
}
