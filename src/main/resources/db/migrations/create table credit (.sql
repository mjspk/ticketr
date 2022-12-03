create table credit  (
    id int primary key auto_increment,
    user_id int not null,
    credit_amount double not null,
    foreign key (user_id) references user(id)
);