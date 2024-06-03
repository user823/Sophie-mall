package com.sophie.sophiemall.main.repository;

import com.sophie.sophiemall.main.domain.MemberBrandAttention;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 会员关注Repository
 */
@Repository
public interface MemberBrandAttentionRepository extends MongoRepository<MemberBrandAttention,String> {
    MemberBrandAttention findByMemberIdAndBrandId(Long memberId, Long brandId);
    int deleteByMemberIdAndBrandId(Long memberId,Long brandId);
    Page<MemberBrandAttention> findByMemberId(Long memberId, Pageable pageable);
    void deleteAllByMemberId(Long memberId);
}
