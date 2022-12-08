package com.dev.recommend.controller;

import com.dev.recommend.dto.RestaurantDto;
import com.dev.recommend.dto.RestaurantListDto;
import com.dev.recommend.entity.Restaurant;
import com.dev.recommend.service.RecommendFoodService;
import com.dev.recommend.service.RecommendRestaurantService;
import com.dev.register.dto.FoodListDto;
import com.dev.utils.response.BaseException;
import com.dev.utils.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/rcn")
@RequiredArgsConstructor
@Slf4j
public class RecommendController {
    private final RecommendFoodService recommendFoodService;
    private final RecommendRestaurantService recommendRestaurantService;

    @GetMapping("/menu")
    public BaseResponse<FoodListDto> getRecommend(@RequestParam("meal") String meal, @RequestParam("date") String date){
        try{
            LocalDate parseDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            FoodListDto recommend;
            if(meal.equals("breakfast")) {
                recommend = recommendFoodService.getRecommendBreakfast(parseDate);
            }
            else if(meal.equals("lunch")) {
                recommend = recommendFoodService.getRecommendLunch(parseDate);
            }
            else {
                recommend = recommendFoodService.getRecommendDinner(parseDate);
            }
            return new BaseResponse<>(recommend);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/lunch")
    public BaseResponse<FoodListDto> getRecommendLunch(@RequestParam("date") String date){
        try{
            LocalDate parseDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            FoodListDto recommendLunch = recommendFoodService.getRecommendLunch(parseDate);
            return new BaseResponse<>(recommendLunch);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/dinner")
    public BaseResponse<FoodListDto> getRecommendDinner(@RequestParam("date") String date){
        try{
            LocalDate parseDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            FoodListDto recommendDinner = recommendFoodService.getRecommendDinner(parseDate);
            return new BaseResponse<>(recommendDinner);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/res")
    public BaseResponse<RestaurantListDto> getRecommendRestaurant(@RequestParam("menu") String name, @RequestParam("lat") double lat, @RequestParam("lng") double lng){
        try{
            RestaurantListDto restaurant = recommendRestaurantService.findRestaurant(name);
            return new BaseResponse<>(restaurant);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}