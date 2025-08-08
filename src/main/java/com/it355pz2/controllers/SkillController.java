package com.it355pz2.controllers;

import com.it355pz2.dto.SkillResponse;
import com.it355pz2.services.SkillService;
import com.it355pz2.services.impl.SkillServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/skills")
public class SkillController {
    private SkillService skillService;
    @GetMapping
    public ResponseEntity<List<SkillResponse>> getSkills() {
        return ResponseEntity.ok(skillService.getSkills());
    }

}
