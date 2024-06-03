package com.sophie.sophiemall.main.repository;

import com.sophie.sophiemall.main.domain.MemberProductCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品收藏Repository
 */
@Repository
public interface MemberProductCollectionRepository extends MongoRepository<MemberProductCollection,String> {
    MemberProductCollection findByMemberIdAndProductId(Long memberId, Long productId);
    int deleteByMemberIdAndProductId(Long memberId,Long productId);
    Page<MemberProductCollection> findByMemberId(Long memberId, Pageable pageable);
    void deleteAllByMemberId(Long memberId);
}
