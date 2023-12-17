package org.dudinskiy.giftcert.model;

import org.dudinskiy.giftcert.entity.Tag;
import org.dudinskiy.giftcert.exception.DaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class TagDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final String INSERT_TAG = "INSERT INTO tag (name) VALUES (?)";
    private final String INSERT_TAG_TO_CERT_LINK = "INSERT INTO gift_certificate_tag (gift_cert_id, tag_id) VALUES ";
    private final String SELECT_ALL_TAGS = "SELECT * FROM tag";
    private final String BY_NAMES = " WHERE name IN (";
    private final String SELECT_ALL_TAGS_BY_NAMES = SELECT_ALL_TAGS + BY_NAMES;
    private final String SELECT_ALL_TAGS_BY_CERT_ID = "SELECT tag.tag_id, tag.name FROM tag" +
            " JOIN gift_certificate_tag gst ON tag.tag_id = gst.tag_id" +
            " WHERE gift_cert_id = ?";
    private final String DELETE_ALL_TAGS_BY_NAMES = "DELETE FROM tag WHERE name IN (";
    private final String DELETE_ALL_TAGS_LINKS_BY_IDS = "DELETE FROM gift_certificate_tag WHERE tag_id IN (";
    private final String DELETE_ALL_CERTS_LINKS_BY_ID = "DELETE FROM gift_certificate_tag WHERE gift_cert_id=?";
    private final RowMapper<Tag> rowMapper = (rs, rowNum) -> {
        Tag tag = new Tag();
        tag.setTagId(rs.getLong("tag_id"));
        tag.setName(rs.getString("name"));
        return tag;
    };

    public Tag insertTag(Tag tag) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(INSERT_TAG, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, tag.getName());
                return ps;
            }, keyHolder);

            Map<String, Object> keys =keyHolder.getKeys();
            if (keys != null) {
                tag.setTagId((Long) keys.get("tag_id"));
            }
            return tag;
        } catch (DataAccessException e) {
            throw new DaoException("INS-TAG", "Exception inserting " + tag, e);
        }
    }

    public List<Tag> getAllTags() {
        try {
            return jdbcTemplate.query(SELECT_ALL_TAGS, rowMapper);
        } catch (DataAccessException e) {
            throw new DaoException("GET-ALL-TAGS", "Exception selecting all tags ", e);
        }
    }

    public List<Tag> getTagsByNames(String[] names) {
        if (names.length == 0) return new ArrayList<>();
        try {
            return jdbcTemplate.query(SELECT_ALL_TAGS_BY_NAMES + generateParamStr(names.length), rowMapper, names);
        } catch (DataAccessException e) {
            throw new DaoException("GET-TAGS-BY-NAMES", "Exception selecting tags by names " + Arrays.toString(names), e);
        }
    }

    public List<Tag> getTagsForCertificate(Long giftCertId) {
        try {
            return jdbcTemplate.query(SELECT_ALL_TAGS_BY_CERT_ID, rowMapper, giftCertId);
        } catch (DataAccessException e) {
            throw new DaoException("GET-TAGS-BY-CERT", "Exception selecting tags by cert Id=" + giftCertId, e);
        }
    }

    public Integer deleteTags(String[] names) {
        try {
            return jdbcTemplate.update(DELETE_ALL_TAGS_BY_NAMES + generateParamStr(names.length), names);
        } catch (DataAccessException e) {
            throw new DaoException("DEL-TAGS-BY-NAMES", "Exception deleting tags by names " + Arrays.toString(names), e);
        }
    }

    private String generateParamStr(int quantity) {
        return "?,".repeat(quantity).replaceFirst(",$", ")");
    }

    public void deleteLinksToCertsBy(String[] names) {
        List<Tag> tags = getTagsByNames(names);
        if (tags.isEmpty()) return;
        Long[] tagIds = tags.stream().map(Tag::getTagId).toArray(Long[]::new);
        try {
            jdbcTemplate.update(DELETE_ALL_TAGS_LINKS_BY_IDS + generateParamStr(tagIds.length), tagIds);
        } catch (DataAccessException e) {
            throw new DaoException("DEL-TAGS-LINKS-BY-IDS", "Exception deleting tags links by tag Ids " + Arrays.toString(tagIds), e);
        }
    }

    public Integer linkTagsToGiftCertificate(Long[] tagIds, Long giftCertId) {
        if (tagIds.length == 0) return 0;
        try {
            return jdbcTemplate.update(INSERT_TAG_TO_CERT_LINK + generateLinkStr(tagIds.length), intersperse(giftCertId, tagIds));
        } catch (DataAccessException e) {
            throw new DaoException("INS-TAGS-TO-CERT-LINKS", "Exception inserting tags to cert links: " + Arrays.toString(tagIds) + " to " + giftCertId, e);
        }
    }

    private Long[] intersperse(Long giftCertId, Long[] tagIds) {
        Long[] result = new Long[tagIds.length * 2];
        int i = 0;
        for (Long tagId : tagIds) {
            result[i++] = giftCertId;
            result[i++] = tagId;
        }
        return result;
    }

    private String generateLinkStr(int quantity) {
        return ("(?,?),").repeat(quantity).replaceFirst(",$", "");
    }

    public Integer unlinkAllTagsByCertId(Long giftCertId) {
        try {
            return jdbcTemplate.update(DELETE_ALL_CERTS_LINKS_BY_ID, giftCertId);
        } catch (DataAccessException e) {
            throw new DaoException("DEL-CERTS-LINKS-BY-ID", "Exception deleting certs links by cert Id=" + giftCertId, e);
        }
    }
}
