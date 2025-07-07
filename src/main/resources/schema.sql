-- pgvector extension 활성화
CREATE EXTENSION IF NOT EXISTS vector;

-- 0 벡터를 생성하는 함수
CREATE OR REPLACE FUNCTION zero_vector(dim integer)
RETURNS vector AS $$
    SELECT array_fill(0::float4, ARRAY[dim])::vector
$$ LANGUAGE sql IMMUTABLE;

-- Restaurant 테이블 생성
CREATE TABLE restaurant (
    id BIGSERIAL PRIMARY KEY,
    place_id VARCHAR(255) UNIQUE,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    thumbnail VARCHAR(255),
    vibe_vector vector(384) DEFAULT zero_vector(384),
    food_vector vector(384) DEFAULT zero_vector(384),
    companion_vector vector(384) DEFAULT zero_vector(384),
    purpose_vector vector(384) DEFAULT zero_vector(384),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CrawlingReview 테이블 생성
CREATE TABLE crawling_review (
    id BIGSERIAL PRIMARY KEY,
    content TEXT,
    hash VARCHAR(255),
    restaurant_id BIGINT REFERENCES restaurant(id),
    vibe_vector vector(384) DEFAULT zero_vector(384),
    food_vector vector(384) DEFAULT zero_vector(384),
    companion_vector vector(384) DEFAULT zero_vector(384),
    purpose_vector vector(384) DEFAULT zero_vector(384)
);
