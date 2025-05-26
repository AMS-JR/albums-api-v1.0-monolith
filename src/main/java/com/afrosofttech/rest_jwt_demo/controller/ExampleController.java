//package com.afrosofttech.rest_jwt_demo.controller;
//
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/users")
//public class ExampleController {
//
//    @GetMapping
//    public List<ExamplePayload> getAllExamples() {} // similar to Ruby's `index`
//
//    @GetMapping("/{id}")
//    public ExamplePayload getExamplerById(@PathVariable Long id) {} // similar to `show`
//
//    @PostMapping
//    public ExamplePayload createExample(@RequestBody ExampleDto exampleDto) {
//        ExamplePayload payload = exampleService.create(dto);
//        return ResponseBuilder.created("Example created successfully", payload);
//    } // `create`
//
//    @PutMapping("/{id}")
//    public ExamplePayload updateExample(@PathVariable Long id, @RequestBody ExampleDto exampleDto) {} // `update`
//
//    @DeleteMapping("/{id}")
//    public void deleteExample(@PathVariable Long id) {} // `destroy`
//}
