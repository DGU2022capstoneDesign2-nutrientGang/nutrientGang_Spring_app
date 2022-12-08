package com.dev.health.service;

import com.dev.health.dto.*;
import com.dev.health.entity.HealthStatus;
import com.dev.health.entity.NutrientStatus;
import com.dev.health.repository.HealthStatusRepository;
import com.dev.health.repository.NutrientStatusRepository;
import com.dev.jwt.utils.SecurityUtil;
import com.dev.member.entity.Member;
import com.dev.member.repository.MemberRepository;
import com.dev.register.entity.Breakfast;
import com.dev.register.entity.Dinner;
import com.dev.register.entity.Food;
import com.dev.register.entity.Lunch;
import com.dev.register.repository.BreakfastRepository;
import com.dev.register.repository.DinnerRepository;
import com.dev.register.repository.FoodRepository;
import com.dev.register.repository.LunchRepository;
import com.dev.utils.response.BaseException;
import com.dev.utils.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthService {
    private final HealthStatusRepository healthStatusRepository;
    private final NutrientStatusRepository nutrientStatusRepository;

    private final BreakfastRepository breakfastRepository;
    private final LunchRepository lunchRepository;
    private final DinnerRepository dinnerRepository;
    private final FoodRepository foodRepository;

    private final MemberRepository memberRepository;

    /**
     * 다이어리 : 칼로리 조회
     * return 섭취한 칼로리와 권장 칼로리
     * */
    @Transactional
    public CalorieInfoResDto getCalorieInfo(LocalDate date) {
        Long userId = SecurityUtil.getCurrentMemberId();
        log.info("유저 아이디 ==> {}",userId);
        Integer needCalorie = healthStatusRepository.findNeedCalorieByMemberAndDate(userId, date);
        if (needCalorie == null) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_HEALTH_STATUS);
        }

        Integer haveCalorie = nutrientStatusRepository.findHaveCalorieByMemberAndDate(userId, date);
        if (haveCalorie == null) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_NUTRIENT_STATUS);
        }

        return CalorieInfoResDto.builder()
                .needCalorie(needCalorie)
                .haveCalorie(haveCalorie)
                .build();
    }

    /**
     * 다이어리 : 영양정보 조회
     * return 섭취한 영양소와 권장 영양소
     * */
    @Transactional
    public NutrientStatusInfoResDto getNutrientInfo(LocalDate date) {
        Long userId = SecurityUtil.getCurrentMemberId();

        // 섭취해야할 영양소 계산
        Optional<NutrientDto> needNutrientInfo = healthStatusRepository.findNeedNutrientByMemberAndDate(userId, date);
        if (needNutrientInfo.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_HEALTH_STATUS);
        }
        NutrientDto needNutrient = needNutrientInfo.get();


        // 섭취한 영양소 값
        Optional<NutrientDto> haveNutrientInfo = nutrientStatusRepository.findHaveNutrientInfoByMemberAndDate(userId, date);
        if (haveNutrientInfo.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_NUTRIENT_STATUS);
        }
        NutrientDto haveNutrient = haveNutrientInfo.get();

        return NutrientStatusInfoResDto.builder()
                .needNutrient(needNutrient)
                .haveNutrient(haveNutrient)
                .build();

    }
    /**
     * 다이어리 : 식사 정보 조회
     * return 아침,점심,저녁 권장 칼로리 및 섭취 칼로리 / 아침 점심 저녁 식사 정보 및 이미지 url 정보
     * TODO: 각 식사 기록에 ID 값 부여하여 리턴하도록 하기
     * */
    @Transactional
    public IntakeMealInfoResDto getIntakeMealInfo(LocalDate date) {
        Long userId = SecurityUtil.getCurrentMemberId();
        IntakeMealInfoResDto intakeMealInfoResDto = new IntakeMealInfoResDto();

        List<Breakfast> breakfastList = breakfastRepository.findByMemberAndDate(userId, date);
        List<Lunch> lunchList = lunchRepository.findByMemberAndDate(userId, date);
        List<Dinner> dinnerList = dinnerRepository.findByMemberAndDate(userId, date);
        Integer findEachNeedCalorie = healthStatusRepository.findNeedCalorieByMemberAndDate(userId, date);
        if (findEachNeedCalorie == null){
            throw new BaseException(BaseResponseStatus.NOT_FOUND_HEALTH_STATUS);
        }
        int eachNeedCalorie = findEachNeedCalorie.intValue();
        intakeMealInfoResDto.setEachNeedCalorie(eachNeedCalorie);

        double breakfastCalorie = 0;
        double lunchCalorie = 0;
        double dinnerCalorie = 0;

        for (Breakfast breakfast : breakfastList) {
            String imgUrl = breakfast.getImgUrl();
            List<String> menus = breakfast.getMenu();
            int calorie = breakfast.getCalories()
                    .stream()
                    .mapToInt(Integer::parseInt)
                    .sum();
            MealInfoDto mealInfoDto = MealInfoDto.builder()
                    .mealId(breakfast.getId())
                    .names(menus)
                    .imgUrl(imgUrl)
                    .calorie(calorie)
                    .build();
            intakeMealInfoResDto.setBreakfast(mealInfoDto);
            breakfastCalorie += calorie;
        }

        for (Lunch lunch : lunchList) {
            String imgUrl = lunch.getImgUrl();
            List<String> menus = lunch.getMenu();
            int calorie = lunch.getCalories()
                    .stream()
                    .mapToInt(Integer::parseInt)
                    .sum();
            MealInfoDto mealInfoDto = MealInfoDto.builder()
                    .mealId(lunch.getId())
                    .names(menus)
                    .imgUrl(imgUrl)
                    .calorie(calorie)
                    .build();
            intakeMealInfoResDto.setLunch(mealInfoDto);
            lunchCalorie += calorie;

        }

        for (Dinner dinner : dinnerList) {
            String imgUrl = dinner.getImgUrl();
            List<String> menus = dinner.getMenu();
            int calorie = dinner.getCalories()
                    .stream()
                    .mapToInt(Integer::parseInt)
                    .sum();
            MealInfoDto mealInfoDto = MealInfoDto.builder()
                    .mealId(dinner.getId())
                    .names(menus)
                    .imgUrl(imgUrl)
                    .calorie(calorie)
                    .build();
            intakeMealInfoResDto.setDinner(mealInfoDto);
            dinnerCalorie += calorie;
        }

        intakeMealInfoResDto.setBreakfastCalorie((int) breakfastCalorie);
        intakeMealInfoResDto.setLunchCalorie((int) lunchCalorie);
        intakeMealInfoResDto.setDinnerCalorie((int) dinnerCalorie);

        return intakeMealInfoResDto;

    }

    /**
     * 리포트 (변화추이) 칼로리 변화
     * return 칼로리의 변화 추이와 섭취,권장 칼로리 및 최대 최소 칼로리
     * */
    @Transactional
    public CalorieReportResDto getCalorieReportInfo() {
        Long userId = SecurityUtil.getCurrentMemberId();

        Integer maxCalorie = nutrientStatusRepository.findMaxCalorieByMember(userId);
        Integer minCalorie = nutrientStatusRepository.findMinCalorieByMember(userId);
        List<CalorieInfoDto> allCalorieInfo = nutrientStatusRepository.findAllCalorieInfoByMember(userId);
        Integer currentNeedCalorie = healthStatusRepository.findNeedCalorieByMemberAndDate(userId, LocalDate.now());
        Optional<NutrientStatus> todayNutrientStatus = nutrientStatusRepository.findByMemberAndDate(userId, LocalDate.now());

        if (maxCalorie == null || minCalorie == null || allCalorieInfo.isEmpty() || todayNutrientStatus.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_NUTRIENT_STATUS);
        }

        if (currentNeedCalorie == null) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_HEALTH_STATUS);
        }


        return CalorieReportResDto.builder()
                .maxCalorie(maxCalorie)
                .minCalorie(minCalorie)
                .needCalorie(currentNeedCalorie)
                .todayCalorie(todayNutrientStatus.get().getCalorie())
                .calorieInfoList(allCalorieInfo)
                .build();
    }

    /**
     * 리포트 (변화추이) 몸무게 변화
     * return 몸무게의 변화 추이. 최대 최소 몸무게, 오늘 몸무게
     * */
    @Transactional
    public WeightReportResDto getWeightReportInfo() {
        Long userId = SecurityUtil.getCurrentMemberId();
        Integer maxWeight = healthStatusRepository.findMaxWeightByMember(userId);
        Integer minWeight = healthStatusRepository.findMinWeightByMember(userId);
        List<WeightInfoDto> allWeightInfo = healthStatusRepository.findAllWeightInfoByMember(userId);
        Optional<HealthStatus> todayHealthStatus = healthStatusRepository.findByMemberAndDate(userId, LocalDate.now());

        if (maxWeight == null || minWeight == null || allWeightInfo.isEmpty() || todayHealthStatus.isEmpty()){
            throw new BaseException(BaseResponseStatus.NOT_FOUND_HEALTH_STATUS);
        }


        return WeightReportResDto.builder()
                .maxWeight(maxWeight)
                .minWeight(minWeight)
                .todayWeight(todayHealthStatus.get().getWeight())
                .weightInfoList(allWeightInfo)
                .build();

    }

    /**
     * 리포트 (영양균형) 섭취 영양소 비율 조회
     * return 일주일 동안 섭취한 탄수화물, 단백질, 지방 비율
     * */
    @Transactional
    public NutrientWeekPortionDto getNutrientPortionInfo(LocalDate begin, LocalDate end) {
        Long userId = SecurityUtil.getCurrentMemberId();
        List<NutrientInfoDto> weekHaveNutrientInfo = nutrientStatusRepository.findHaveNutrientInfoByMemberAndPeriod(userId, begin, end);
        // 리스트가 비어있어도 계산은 해야하기 때문에 예외처리 하지 않음
        return calculateNutrientWeekPortion(weekHaveNutrientInfo);

    }

    /**
     * 리포트 (변화추이) 권장/섭취 상세정보 조회
     * return 평균 섭취 영양소와 권장 섭취 영양소의 평균 그리고 그에 따른 섭취 비율
     * */
    @Transactional
    public NutrientWeekInfoResDto getNutrientWeekInfo(LocalDate begin, LocalDate end) {
        Long userId = SecurityUtil.getCurrentMemberId();
        List<NutrientInfoDto> weekHaveNutrientInfo = nutrientStatusRepository.findHaveNutrientInfoByMemberAndPeriod(userId, begin, end);
        List<NutrientInfoDto> weekNeedNutrientInfo = healthStatusRepository.findNeedNutrientInfoByMemberAndPeriod(userId, begin, end);
        if (weekHaveNutrientInfo.isEmpty()){
            throw new BaseException(BaseResponseStatus.NOT_FOUND_NUTRIENT_STATUS);
        }
        if (weekNeedNutrientInfo.isEmpty()){
            throw new BaseException(BaseResponseStatus.NOT_FOUND_HEALTH_STATUS);
        }

        NutrientDto weekNeedNutrientAvg = calculateWeekNutrient(weekNeedNutrientInfo);
        NutrientDto weekHaveNutrientAvg = calculateWeekNutrient(weekHaveNutrientInfo);

        double carbohydratePortion = (double) weekHaveNutrientAvg.getCarbohydrate() / weekNeedNutrientAvg.getCarbohydrate();
        double proteinPortion = (double) weekHaveNutrientAvg.getProtein() / weekNeedNutrientAvg.getProtein();
        double fatPortion = (double) weekHaveNutrientAvg.getFat() / weekNeedNutrientAvg.getFat();

        carbohydratePortion = Double.parseDouble(String.format("%.2f", carbohydratePortion));
        proteinPortion = Double.parseDouble(String.format("%.2f", proteinPortion));
        fatPortion = Double.parseDouble(String.format("%.2f", fatPortion));

        NutrientWeekPortionDto nutrientPortion = NutrientWeekPortionDto.builder()
                .carbohydratePortion(carbohydratePortion)
                .proteinPortion(proteinPortion)
                .fatPortion(fatPortion)
                .build();
        return NutrientWeekInfoResDto.builder()
                .haveNutrient(weekHaveNutrientAvg)
                .needNutrient(weekNeedNutrientAvg)
                .nutrientPortion(nutrientPortion)
                .build();
    }

    /**
     * 리포트 (변화추이) 일주일 영양소 비율 그래프 조회
     * return 일주일 섭취 영양소 비율 리스트
     * */
    @Transactional
    public NutrientWeekPortionInfoResDto getWeekNutrientPortion(LocalDate begin, LocalDate end) {
        Long userId = SecurityUtil.getCurrentMemberId();
        LocalDate date = begin;
        List<NutrientWeekPortionInfoDto> portionList = new ArrayList<>();
        while (!date.equals(end.plusDays(1))) {
            Optional<NutrientInfoDto> findHaveNutrientWithDateByMemberAndDate = nutrientStatusRepository.findHaveNutrientWithDateByMemberAndDate(userId, date);
            if (findHaveNutrientWithDateByMemberAndDate.isEmpty()) {
                portionList.add(NutrientWeekPortionInfoDto.builder()
                        .date(date)
                        .carbohydratePortion(0.00)
                        .proteinPortion(0.00)
                        .fatPortion(0.00)
                        .build());
                date = date.plusDays(1);
                continue;
            }
            NutrientInfoDto nutrientInfo = findHaveNutrientWithDateByMemberAndDate.get();
            int sumVal = nutrientInfo.getCarbohydrate() + nutrientInfo.getProtein() + nutrientInfo.getFat();
            if (sumVal == 0) {
                portionList.add(NutrientWeekPortionInfoDto.builder()
                        .date(date)
                        .carbohydratePortion(0.00)
                        .proteinPortion(0.00)
                        .fatPortion(0.00)
                        .build());
                date = date.plusDays(1);
                continue;
            }
            portionList.add(NutrientWeekPortionInfoDto.builder()
                    .date(nutrientInfo.getDate())
                    .carbohydratePortion((double) (nutrientInfo.getCarbohydrate()) / sumVal)
                    .proteinPortion((double) (nutrientInfo.getProtein()) / sumVal)
                    .fatPortion((double) (nutrientInfo.getFat()) / sumVal)
                    .build());
            date = date.plusDays(1);
        }

        return NutrientWeekPortionInfoResDto.builder().weekPortionList(portionList).build();
    }


    /**
     * 리포트 (식사여부) 식사 기록 그래프 조회 성공
     * return 총 식사횟수의 아침,점심,저녁 비율
     * */
    @Transactional
    public MealPortionResDto getMealPortion(LocalDate begin, LocalDate end) {
        Long userId = SecurityUtil.getCurrentMemberId();
        LocalDate date = begin;
        int breakfastCount = 0;
        int lunchCount = 0;
        int dinnerCount = 0;
        while (!date.equals(end.plusDays(1))) {
            List<Breakfast> breakfastList = breakfastRepository.findByMemberAndDate(userId, date);
            List<Lunch> lunchList = lunchRepository.findByMemberAndDate(userId, date);
            List<Dinner> dinnerList = dinnerRepository.findByMemberAndDate(userId, date);
            if (!breakfastList.isEmpty()) {
                breakfastCount++;
            }
            if (!lunchList.isEmpty()) {
                lunchCount++;
            }
            if (!dinnerList.isEmpty()) {
                dinnerCount++;
            }
            date = date.plusDays(1);
        }
        int sumCount = breakfastCount + lunchCount + dinnerCount;
        if (sumCount == 0) {
            throw new BaseException(BaseResponseStatus.ZERO_DATA_INFO);
        }
        return MealPortionResDto.builder()
                .breakfast((double) breakfastCount / sumCount)
                .lunch((double) lunchCount / sumCount)
                .dinner((double) dinnerCount / sumCount)
                .build();
    }

    /**
     * 리포트 (식사여부) 식사여부 리스트 조회
     * return 특정 기간 식사여부 리스트
     * */
    @Transactional
    public HadMealInfoResDto getHadMealInfo(LocalDate begin, LocalDate end) {
        Long userId = SecurityUtil.getCurrentMemberId();
        Member member = memberRepository.findById(userId).get();
        LocalDate date = begin;
        List<HadMealDto> breakfastList = new ArrayList<>();
        List<HadMealDto> lunchList = new ArrayList<>();
        List<HadMealDto> dinnerList = new ArrayList<>();
        int id = 1;
        while (!date.equals(end.plusDays(1))) {

            boolean breakfastIsEaten = breakfastRepository.existsByMemberAndDate(member, date);
            boolean lunchIsEaten = lunchRepository.existsByMemberAndDate(member, date);
            boolean dinnerIsEaten = dinnerRepository.existsByMemberAndDate(member, date);

            breakfastList.add(HadMealDto.builder().id(id).isEaten(breakfastIsEaten).date(date).build());
            lunchList.add(HadMealDto.builder().id(id).isEaten(lunchIsEaten).date(date).build());
            dinnerList.add(HadMealDto.builder().id(id).isEaten(dinnerIsEaten).date(date).build());

            id++;
            date = date.plusDays(1);
        }
        return HadMealInfoResDto.builder()
                .breakfastList(breakfastList)
                .lunchList(lunchList)
                .dinnerList(dinnerList)
                .build();
    }

    @Transactional
    public void createStatuses() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.plusDays(-1);
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            Optional<HealthStatus> yesterdayInfo = healthStatusRepository.findByMemberAndDate(member.getId(), yesterday);
            if (yesterdayInfo.isPresent()) {
                HealthStatus healthStatus = HealthStatus.builder()
                        .date(today)
                        .gender(yesterdayInfo.get().getGender())
                        .height(yesterdayInfo.get().getHeight())
                        .weight(yesterdayInfo.get().getWeight())
                        .activity(yesterdayInfo.get().getActivity())
                        .target(yesterdayInfo.get().getTarget())
                        .build();
                healthStatus.setMember(member);
                healthStatus.setNeedCalorie();
                healthStatus.setNeedNutrients();
                healthStatusRepository.save(healthStatus);
            }
            NutrientStatus nutrientStatus = NutrientStatus.builder()
                    .calorie(0)
                    .carbohydrate(0)
                    .protein(0)
                    .fat(0)
                    .date(today)
                    .build();
            nutrientStatus.setMember(member);
            if (nutrientStatusRepository.findByMemberAndDate(member.getId(),today).isEmpty()) {
                nutrientStatusRepository.save(nutrientStatus);
            }
        }
    }

    private NutrientDto calculateWeekNutrient(List<NutrientInfoDto> weekNutrientInfo) {
        int carbohydrateAvg = (int) (weekNutrientInfo.stream()
                .map(NutrientInfoDto::getCarbohydrate)
                .mapToInt(Integer::intValue)
                .filter(v -> v != 0)
                .average()
                .orElse(0));
        int proteinAvg = (int) (weekNutrientInfo.stream()
                .map(NutrientInfoDto::getProtein)
                .mapToInt(Integer::intValue)
                .filter(v -> v != 0)
                .average()
                .orElse(0));
        int fatSumAvg = (int) (weekNutrientInfo.stream()
                .map(NutrientInfoDto::getFat)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .filter(v -> v != 0)
                .average()
                .orElse(0));
        return new NutrientDto(carbohydrateAvg, proteinAvg, fatSumAvg);

    }

    private NutrientWeekPortionDto calculateNutrientWeekPortion(List<NutrientInfoDto> weekNutrientInfo) {
        int carbohydrateSum = 0;
        int proteinSum = 0;
        int fatSum = 0;
        for (NutrientInfoDto nutrientInfoDto : weekNutrientInfo) {
            carbohydrateSum += nutrientInfoDto.getCarbohydrate();
            proteinSum += nutrientInfoDto.getProtein();
            fatSum += nutrientInfoDto.getFat();
        }
        int wholeNutrient = carbohydrateSum + proteinSum + fatSum;
        if (wholeNutrient == 0) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_INTAKE);
        }
        double carbohydratePortion = (double) carbohydrateSum / wholeNutrient;
        double proteinPortion = (double) proteinSum / wholeNutrient;
        double fatPortion = (double) fatSum / wholeNutrient;
        carbohydratePortion = Double.parseDouble(String.format("%.2f", carbohydratePortion));
        proteinPortion = Double.parseDouble(String.format("%.2f", proteinPortion));
        fatPortion = Double.parseDouble(String.format("%.2f", fatPortion));
        return NutrientWeekPortionDto.builder()
                .carbohydratePortion(carbohydratePortion)
                .proteinPortion(proteinPortion)
                .fatPortion(fatPortion)
                .build();
    }

}
