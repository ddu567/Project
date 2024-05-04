package dev.project.orderservice.client;

import dev.project.userservice.entity.Member;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface MemberServiceClient {
    @GetMapping("/members/{id}")
    Member getMemberById(@PathVariable("id") Long id);
}
