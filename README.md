# cristalix-lootbox
Мод для клиента [cristalix.ru](https://cristalix.ru), добавляющий лутбоксы.

## Использование
* Отправить мод клиенту игрока
* Отправить PluginMessage в канал lootbox:open, например с использованием [sharkly](https://github.com/anfanik/sharkly):
  ```java
  PluginMessage.builder()
            .integer(loots)   # Количество выбитых предметов
            .item(item)       # Иконка выбитого предмета
            .string(name)     # Название предмета
            .string(rarity)   # Редкость предмета (NOTHING, COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, INCREDIBLE)
            .build().send("lootbox:open", player);
  ```
  Если выбитых предметов несколько, то они добавляются в PluginMessage друг за другом.