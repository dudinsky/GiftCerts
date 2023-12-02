package org.dudinskiy.giftcert.model;

import org.dudinskiy.giftcert.entity.GiftCert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GiftCertDao {

    @Autowired
    NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final String INSERT_GIFT_CERT = "INSERT INTO gift_certificate " +
            "( name, description, price, duration, create_date) VALUES " +
            "(:name,:description,:price,:duration,:createDate)";
    private final String SELECT_ALL_GIFT_CERTS = "SELECT * FROM gift_certificate";
    private final String BY_NAMES = " WHERE name IN (";
    private final String SELECT_ALL_GIFT_CERTS_BY_NAME = SELECT_ALL_GIFT_CERTS + BY_NAMES;
    private final String UPDATE_GIFT_CERT = "UPDATE gift_certificate SET ";
    private final String BY_ID = "WHERE gift_cert_id=:giftCertId";
    private final String DELETE_ALL_GIFT_CERTS_BY_NAMES = "DELETE FROM gift_certificate WHERE name IN (";
    private final String DELETE_ALL_GIFT_CERTS_LINKS_BY_IDS = "DELETE FROM gift_certificate_tag WHERE gift_cert_id IN (";

    private final RowMapper<GiftCert> rowMapper = (rs, rowNum) -> {
        GiftCert giftCert = new GiftCert();
        giftCert.setGiftCertId(rs.getLong("gift_cert_id"));
        giftCert.setName(rs.getString("name"));
        giftCert.setDescription(rs.getString("description"));
        giftCert.setPrice(rs.getBigDecimal("price"));
        giftCert.setDuration(rs.getInt("duration"));
        Timestamp createDate = rs.getTimestamp("create_date");
        if (createDate != null) {
            giftCert.setCreateDate(createDate.toLocalDateTime());
        }
        Timestamp lastUpdateDate = rs.getTimestamp("last_update_date");
        if (lastUpdateDate != null) {
            giftCert.setLastUpdateDate(lastUpdateDate.toLocalDateTime());
        }
        return giftCert;
    };

    public GiftCert insertGiftCert(GiftCert giftCert) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        giftCert.setCreateDate(LocalDateTime.now());
        namedJdbcTemplate.update(INSERT_GIFT_CERT, new BeanPropertySqlParameterSource(giftCert), keyHolder);
        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null) {
            giftCert.setGiftCertId((Long) keys.get("gift_cert_id"));
        }
        return giftCert;
    }

    public List<GiftCert> getAllGiftCerts() {
        return jdbcTemplate.query(SELECT_ALL_GIFT_CERTS, rowMapper);
    }

    public List<GiftCert> getGiftCertsByNames(String[] names) {
        if (names.length == 0) return new ArrayList<>();
        return jdbcTemplate.query(SELECT_ALL_GIFT_CERTS_BY_NAME + generateParamStr(names.length), rowMapper, names);
    }

    public List<GiftCert> searchGiftCertsBy(String tagName, String certNamePart, String certDescPart, String sortByDate, String sortByName) {
        StringBuilder sql = new StringBuilder(SELECT_ALL_GIFT_CERTS);
        HashMap<String, String> params = new HashMap<>();
        boolean anyCriteriaWasAdded = false;
        if (StringUtils.hasLength(tagName)) {
            sql.append(" WHERE gift_cert_id IN (");
            sql.append("     SELECT gift_cert_id FROM gift_certificate_tag WHERE tag_id IN (");
            sql.append("         SELECT tag_id FROM tag WHERE name = :tagName");
            sql.append("     )");
            sql.append(" )");
            anyCriteriaWasAdded = true;
            params.put("tagName", tagName);
        }
        if (StringUtils.hasLength(certNamePart)) {
            if (anyCriteriaWasAdded) {
                sql.append(" AND name ILIKE :certNamePart");
            } else {
                sql.append(" WHERE name ILIKE :certNamePart");
                anyCriteriaWasAdded = true;
            }
            params.put("certNamePart", "%" + certNamePart + "%");
        }
        if (StringUtils.hasLength(certDescPart)) {
            if (anyCriteriaWasAdded) {
                sql.append(" AND description ILIKE :certDescPart");
            } else {
                sql.append(" WHERE description ILIKE :certDescPart");
                anyCriteriaWasAdded = true;
            }
            params.put("certDescPart", "%" + certDescPart + "%");
        }
        boolean orderWasAdded = false;
        if ("ASC".equalsIgnoreCase(sortByDate)) {
            sql.append(" ORDER BY create_date ASC");
            orderWasAdded = true;
        } else if ("DESC".equalsIgnoreCase(sortByDate)) {
            sql.append(" ORDER BY create_date DESC");
            orderWasAdded = true;
        }
        if ("ASC".equalsIgnoreCase(sortByName)) {
            if (orderWasAdded) {
                sql.append(" , name ASC");
            } else {
                sql.append(" ORDER BY name ASC");
                orderWasAdded = true;
            }
        } else if ("DESC".equalsIgnoreCase(sortByName)) {
            if (orderWasAdded) {
                sql.append(" , name DESC");
            } else {
                sql.append(" ORDER BY name DESC");
                orderWasAdded = true;
            }
        }
//        System.out.println("sql = " + sql.toString());
        return namedJdbcTemplate.query(sql.toString(), params, rowMapper);
    }

    private String generateParamStr(int quantity) {
        return "?,".repeat(quantity).replaceFirst(",$", ")");
    }

    public GiftCert updateCert(GiftCert giftCert) {
        StringBuilder sql = new StringBuilder(UPDATE_GIFT_CERT);
        if (giftCert.getName() != null) sql.append("name=:name, ");
        if (giftCert.getDescription() != null) sql.append("description=:description, ");
        if (giftCert.getPrice() != null) sql.append("price=:price, ");
        if (giftCert.getDuration() != null) sql.append("duration=:duration, ");
        sql.append("last_update_date=:lastUpdateDate ").append(BY_ID);
        giftCert.setLastUpdateDate(LocalDateTime.now());
        namedJdbcTemplate.update(sql.toString(), new BeanPropertySqlParameterSource(giftCert));
        return giftCert;
    }

    public Integer deleteGiftCerts(String[] names) {
        return jdbcTemplate.update(DELETE_ALL_GIFT_CERTS_BY_NAMES + generateParamStr(names.length), names);
    }

    public void deleteLinksToTagsByCertNames(String[] names) {
        List<GiftCert> giftCerts = getGiftCertsByNames(names);
        if (giftCerts.isEmpty()) return;
        Long[] giftCertIds = giftCerts.stream().map(GiftCert::getGiftCertId).toArray(Long[]::new);
        jdbcTemplate.update(DELETE_ALL_GIFT_CERTS_LINKS_BY_IDS + generateParamStr(giftCertIds.length), giftCertIds);
    }
}
