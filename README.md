# Introduction
Fully packet based PetsAPI (1.8 - 1.21) using [PacketEvents](https://github.com/retrooper/packetevents/) and [EntityLib](https://github.com/Tofaa2/EntityLib) 

> [!IMPORTANT]
> Project version is required to be Java 16 at least!

# Info
PetsAPI allows you to place floating heads in the world or attach them as pets that follow players — all fully packet-based.

# Events
### PetInteractEvent

Fired when a player interacts with a pet.

**Accessors:**
- `getPlayer()` → The player who clicked the pet
- `getPet()` → The clicked pet

# Setup
1. Clone repo
2. ```run mvn install```
3. Add this to pom.xml
   ```
   <dependency>
       <groupId>dev.lrxh</groupId>
       <artifactId>PetsAPI</artifactId>
       <version>1.0.0</version>
   </dependency>
   ```
# Example Usage
```java
PetsAPI.init(this);

// Example Player Pet
Player player = ...;
PlayerPet pet = new PlayerPet(SkinData.ofPlayerName(player.getName()));
pet.setLookAtPlayer(true);
pet.spawn(player);

// Example World Pet
Pet pet = new Pet(AnimalSkinData.SHEEP);
Location location = ...;
pet.spawn(location);

// Remove player pets
for (Pet pet : PetsAPI.getPets(player)) {
    pet.remove();
}

pet. // See all methods
``` 
