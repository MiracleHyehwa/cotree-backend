UPDATE COTREE.ITEM_CATEGORY t SET t.NAME = '생선과 해산물 건어물' WHERE t.NAME LIKE 'fish' ESCAPE '#';
UPDATE COTREE.ITEM_CATEGORY t SET t.NAME = '밥과 국 면' WHERE t.NAME LIKE 'rice' ESCAPE '#';
UPDATE COTREE.ITEM_CATEGORY t SET t.NAME = '영양제' WHERE t.NAME LIKE 'nutrition' ESCAPE '#';
UPDATE COTREE.ITEM_CATEGORY t SET t.NAME = '양념과 오일 통조림' WHERE t.NAME LIKE 'sauce' ESCAPE '#';
UPDATE COTREE.ITEM_CATEGORY t SET t.NAME = '과자와 초콜릿 캔디' WHERE t.NAME LIKE 'snack' ESCAPE '#';
UPDATE COTREE.ITEM_CATEGORY t SET t.NAME = '베이커리와 치즈' WHERE t.NAME LIKE 'bread#_cheeze' ESCAPE '#';
UPDATE COTREE.ITEM_CATEGORY t SET t.NAME = '육류와 달걀' WHERE t.NAME LIKE 'meat' ESCAPE '#';
UPDATE COTREE.ITEM_CATEGORY t SET t.NAME = '곡물과 견과' WHERE t.NAME LIKE 'crop' ESCAPE '#';
UPDATE COTREE.ITEM_CATEGORY t SET t.NAME = '밑반찬과 간식' WHERE t.NAME LIKE 'side#_dish' ESCAPE '#';
UPDATE COTREE.ITEM_CATEGORY t SET t.NAME = '과일과 채소' WHERE t.NAME LIKE 'fruit#_veg' ESCAPE '#';
commit;