package dev.project.orderservice.client;

import dev.project.orderservice.dto.MemberInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/members/{id}")
    MemberInfoDTO getMemberById(@PathVariable("id") Long id);
}
