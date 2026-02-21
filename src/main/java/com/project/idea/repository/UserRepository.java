package com.project.idea.repository;

import com.project.idea.model.User1;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User1, Long> {
  Optional<User1> findByUsername(String username);

  List<User1> findByRole(String role);
}
