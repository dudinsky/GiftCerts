package org.dudinskiy.giftcert.service;

import org.dudinskiy.giftcert.entity.GiftCert;
import org.dudinskiy.giftcert.entity.Tag;
import org.dudinskiy.giftcert.model.GiftCertDao;
import org.dudinskiy.giftcert.model.TagDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GiftCertService {

    @Autowired
    GiftCertDao giftCertDao;
    @Autowired
    TagDao tagDao;

    @Transactional
    public GiftCert addCert(GiftCert giftCert) {
        GiftCert insertedGiftCert = giftCertDao.insertGiftCert(giftCert);
        processTagsForCertificate(insertedGiftCert);
        return insertedGiftCert;
    }

    private void processTagsForCertificate(GiftCert giftCert) {
        if (giftCert.getTags() == null) return;
        List<String> tagNames = giftCert.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        List<Tag> existingTags = tagDao.getTagByNames(tagNames.toArray(String[]::new));
        List<String> existingTagNames = existingTags.stream().map(Tag::getName).collect(Collectors.toList());
        List<Tag> insertedTags = giftCert.getTags().stream()
                .filter(tag -> !existingTagNames.contains(tag.getName()))
                .map(tagDao::insertTag)
                .collect(Collectors.toList());
        existingTags.addAll(insertedTags);
        tagDao.linkTagsToGiftCertificate(
                existingTags.stream().map(Tag::getTagId).collect(Collectors.toList()).toArray(Long[]::new),
                giftCert.getGiftCertId()
        );
        giftCert.getTags().forEach(
                tag -> tag.setTagId(existingTags.stream()
                        .filter(currTag -> tag.getName().equals(currTag.getName()))
                        .findAny().orElseThrow().getTagId()
                )
        );
    }

    @Transactional(readOnly = true)
    public List<GiftCert> getAllGiftCerts() {
        List<GiftCert> allGiftCerts = giftCertDao.getAllGiftCerts();
        allGiftCerts.forEach(this::populateTagsForCertificate);
        return allGiftCerts;
    }

    @Transactional(readOnly = true)
    public List<GiftCert> getGiftCertsByNames(String[] names) {
        List<GiftCert> giftCertsByNames = giftCertDao.getGiftCertsByNames(names);
        giftCertsByNames.forEach(this::populateTagsForCertificate);
        return giftCertsByNames;
    }

    private void populateTagsForCertificate(GiftCert giftCert) {
        giftCert.setTags(tagDao.getTagsForCertificate(giftCert.getGiftCertId()));
    }

    @Transactional(readOnly = true)
    public List<GiftCert> searchGiftCertsBy(String tagName, String certNamePart, String certDescPart, String sortByDate, String sortByName) {
        List<GiftCert> giftCerts = giftCertDao.searchGiftCertsBy(tagName, certNamePart, certDescPart, sortByDate, sortByName);
        giftCerts.forEach(this::populateTagsForCertificate);
        return giftCerts;
    }

    @Transactional
    public GiftCert updateCert(GiftCert giftCert) {
        if (giftCert.getTags() != null) {
            tagDao.unlinkAllTagsByCertId(giftCert.getGiftCertId());
            processTagsForCertificate(giftCert);
        }
        return giftCertDao.updateCert(giftCert);
    }

    @Transactional
    public Integer deleteGiftCert(String[] names) {
        if (names.length == 0) return 0;
        // TODO: check how cascade deleting works by means of RDBMS on the many-to-many table, and without this explicit deleting:
        giftCertDao.deleteLinksToTagsByCertNames(names);
        return giftCertDao.deleteGiftCerts(names);
    }
}
