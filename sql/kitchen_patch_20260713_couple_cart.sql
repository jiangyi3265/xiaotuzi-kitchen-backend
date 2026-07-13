create table if not exists kitchen_couple_item(
  id bigint primary key auto_increment,
  couple_id bigint not null,
  dish_id bigint not null,
  wx_user_id bigint not null,
  quantity int not null default 1,
  create_time datetime default current_timestamp,
  update_time datetime default current_timestamp,
  unique key uk_couple_dish_user(couple_id,dish_id,wx_user_id),
  key idx_couple(couple_id)
) engine=InnoDB default charset=utf8mb4 comment='情侣空间双方共享点菜';
