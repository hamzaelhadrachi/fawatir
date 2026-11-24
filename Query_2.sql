select * from UserEvents;

select * from Events;

select * from Users;

select * from UserRoles;

update UserRoles set role_id = 3 where id = 1;

select * from Roles;


SELECT uev.id, uev.device, uev.ip_address, ev.type, ev.description, uev.created_at FROM Events ev JOIN UserEvents uev ON ev.id = uev.event_id JOIN Users u ON u.id = uev.user_id WHERE u.id = 1 ORDER BY uev.created_at DESC LIMIT 10