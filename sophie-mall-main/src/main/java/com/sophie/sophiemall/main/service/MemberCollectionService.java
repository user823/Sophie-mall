package com.sophie.sophiemall.main.service;

import com.sophie.sophiemall.main.domain.MemberProductCollection;
import com.sophie.sophiemall.main.domain.MemberProductCollection;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 会员收藏Service
 */
public interface MemberCollectionService {
    int add(MemberProductCollection productCollection);

    int delete(Long productId);

    Page<MemberProductCollection> list(Integer pageNum, Integer pageSize);

    MemberProductCollection detail(Long productId);

    void clear();
}
