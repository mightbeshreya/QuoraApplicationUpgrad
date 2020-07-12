package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

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

    /* List of already posted questions are retrieved from DB
    * getResultList is used for retrieving lists
    *  */
    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("getAllQuestions" , QuestionEntity.class).getResultList();
        }catch(NoResultException nre) {
            return null;
        }
    }
}
