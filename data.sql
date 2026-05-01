-- 1. 사용자 생성 (로그에 생성자 정보인 created_by가 not null이므로 꼭 넣어줘야 합니다)
INSERT INTO p_user (username, nickname, email, password, role, is_public, created_at, created_by, updated_at, updated_by)
VALUES
    ('owner1', '사장님', 'owner@test.com', 'Pass123!', 'OWNER', true, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('customer1', '우수고객', 'customer@test.com', 'Pass123!', 'CUSTOMER', true, now(), 'SYSTEM', now(), 'SYSTEM');

-- 2. 지역(Area) 생성 (Store가 참조함)
INSERT INTO p_area (area_id, city, district, name, is_active, created_at, created_by, updated_at, updated_by)
VALUES ('550e8400-e29b-41d4-a716-446655440001', '서울시', '강남구', '역삼동', true, now(), 'SYSTEM', now(), 'SYSTEM');

-- 3. 가게 카테고리 생성 (StoreCategorySeeder가 이미 넣었을 수 있으니 중복 주의!)
-- 만약 에러가 나면 이 부분만 빼고 실행하세요.
INSERT INTO p_store_category (store_category_id, name, created_at, created_by, updated_at, updated_by)
VALUES ('550e8400-e29b-41d4-a716-446655440002', '치킨', now(), 'SYSTEM', now(), 'SYSTEM');

-- 4. 가게 생성 (사장님 user_id는 1번, 지역/카테고리 ID 위에서 만든 것 사용)
INSERT INTO p_store (store_id, owner_id, store_category_id, area_id, name, address, phone, average_rating, is_hidden, created_at, created_by, updated_at, updated_by)
VALUES ('550e8400-e29b-41d4-a716-446655440003', 1, '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001',
        '스파르타 치킨', '서울시 강남구 역삼로 1', '02-123-4567', 0.0, false, now(), 'SYSTEM', now(), 'SYSTEM');

-- 5. 고객 배송지 생성 (고객 user_id는 2번)
INSERT INTO p_address (id, user_id, alias, address, detail, zip_code, is_default, created_at, created_by, updated_at, updated_by)
VALUES ('550e8400-e29b-41d4-a716-446655440004', 2, '우리집', '서울시 강남구 논현로 123', '202호', '06123', true, now(), 'SYSTEM', now(), 'SYSTEM');

-- 6. 메뉴 카테고리 생성
INSERT INTO p_menu_category (menu_category_id, store_id, name, created_at, created_by, updated_at, updated_by)
VALUES ('550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440003', '대표메뉴', now(), 'SYSTEM', now(), 'SYSTEM');

-- 7. 메뉴 생성 (MoneyVO는 로그 확인 결과 'price' 컬럼으로 생성됨)
INSERT INTO p_menu (menu_id, store_id, menu_category_id, name, price, description, is_hidden, created_at, created_by, updated_at, updated_by)
VALUES ('550e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440005',
        '황금올리브 치킨', 20000, '바삭함의 끝판왕', false, now(), 'SYSTEM', now(), 'SYSTEM');