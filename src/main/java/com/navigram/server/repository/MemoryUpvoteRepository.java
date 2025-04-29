package com.navigram.server.repository;

import com.navigram.server.model.MemoryUpvote;
import com.navigram.server.model.Memory;
import com.navigram.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoryUpvoteRepository extends JpaRepository<MemoryUpvote, Long> {
    boolean existsByMemoryAndUser(Memory memory, User user);
    void deleteByMemoryAndUser(Memory memory, User user);
}
