package com.jbhunt.pickleballmatchmaker.repository;

import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PickleballUserRepository extends MongoRepository<PickleballUser, String> {
    List<PickleballUser> findByZipCode(int zipCode);
    List<PickleballUser> findByUserName(String userName);
    List<PickleballUser> findByName(String name);
    List<PickleballUser> findByAge(int age);
    List<PickleballUser> findBySkillLevel(int skillLevel);
}