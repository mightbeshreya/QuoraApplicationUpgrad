package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/* Repository - DB */
@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    /* QuestionEntity Object is persisted - Question is created  and QuestionEntity object is returned*/
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }
}
