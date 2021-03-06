package com.ssafy.api.domain.repository.plogging;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.api.domain.dao.mountain.Mountain;
import com.ssafy.api.domain.dao.plogging.QPlogging;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PloggingRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    private QPlogging qPlogging = QPlogging.plogging;

    public List<Tuple> rankByDistance(Mountain mountain) {
        List<Tuple> tuples = jpaQueryFactory.select(qPlogging.user, qPlogging.distance.sum()).from(qPlogging)
            .where(qPlogging.mountain.eq(mountain))
            .groupBy(qPlogging.user)
            .orderBy(qPlogging.distance.sum().desc())
            .offset(0)
            .limit(10)
            .fetch();

        for(Tuple tuple : tuples){
            System.out.println(tuple);
        }

        return  tuples;
    }

    public List<Tuple> rankByCount(Mountain mountain) {
        List<Tuple> tuples = jpaQueryFactory.select(qPlogging.user, qPlogging.count()).from(qPlogging)
            .where(qPlogging.mountain.eq(mountain))
            .groupBy(qPlogging.user)
            .orderBy(qPlogging.count().desc())
            .offset(0)
            .limit(10)
            .fetch();

        for(Tuple tuple : tuples){
            System.out.println(tuple);
        }

        return  tuples;
    }
}
