package org.dudinskiy.giftcert.service;

import org.dudinskiy.giftcert.entity.Tag;
import org.dudinskiy.giftcert.model.TagDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    @Autowired
    TagDao tagDao;

    public Tag addTag(Tag tag) {
        return tagDao.insertTag(tag);
    }

    public List<Tag> getAllTags() {
        return tagDao.getAllTags();
    }

    public List<Tag> getTagsByNames(String[] tagNames) {
        return tagDao.getTagByNames(tagNames);
    }

    public Integer deleteTags(String[] names) {
        if (names.length == 0) return 0;
        // TODO: check how cascade deleting works by means of RDBMS on the many-to-many table, and without this explicit deleting:
        tagDao.deleteLinksToCertsBy(names);
        return tagDao.deleteTags(names);
    }
}
