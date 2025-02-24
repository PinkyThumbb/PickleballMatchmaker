package com.jbhunt.pickleballmatchmaker.repository;

import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PickleballUserRepository  extends MongoRepository<PickleballUser, String> {
}
