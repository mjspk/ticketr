



create table movie (
    id int primary key auto_increment,
    title varchar(255) not null,
    description varchar(255) not null,
    genre varchar(255) not null,
    rating varchar(255) not null,
    release_date varchar(255) not null,
    duration varchar(255) not null,
    trailer varchar(255) not null,
    poster varchar(255) not null
);
create table theater (
    id int primary key auto_increment,
    name varchar(255) not null,
    address varchar(255) not null,
    city varchar(255) not null,
    province varchar(255) not null,
    postal_code varchar(255) not null,
    phone varchar(255) not null,
    email varchar(255) not null,
    website varchar(255) not null

);
create table user (
    id int primary key auto_increment,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    email varchar(255) not null,
    address varchar(255) not null,
    city varchar(255) not null,
    province varchar(255) not null,
    postal_code varchar(255) not null,
    phone varchar(255) not null,
    password varchar(255) not null
);
create table seat (
    id int primary key auto_increment,
    theater_id int not null,
    row_number int not null,
    seat_number int not null,
    price int not null,
    status varchar(255) not null,
    foreign key (theater_id) references theater(id),
    unique (theater_id, row_number, seat_number)
);

create table showtime (
    id int primary key auto_increment,
    date varchar(255) not null,
    time varchar(255) not null,
    theater_id int not null,
    movie_id int not null,
    foreign key (theater_id) references theater(id),
    foreign key (movie_id) references movie(id)

);



create table ticket (
    id int primary key auto_increment,
    user_id int not null,
    showtime_id int not null,
    seat_id int not null,
    price int not null,
    status varchar(255) not null,
    foreign key (user_id) references user(id),
    foreign key (showtime_id) references showtime(id),
    foreign key (seat_id) references seat(id)

);

create table role (
    id int primary key auto_increment,
    name varchar(255) not null
);

create table user_role (
    id int primary key auto_increment,
    user_id int not null,
    role_id int not null,
    foreign key (user_id) references user(id),
    foreign key (role_id) references role(id)
);


create table news (
    id int primary key auto_increment,
    title varchar(255) not null,
    theNews varchar(255) not null,
    image varchar(255) not null,
  
);
<<<<<<< HEAD
=======

>>>>>>> e25aaa2575970fa236d3d782a2244ee291aea2fd
