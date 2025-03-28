# PetsAPI
<div align="center">
</div>

# Introduction
Fully packet based PetsAPI (1.8 - 1.21) using [PacketEvents](https://github.com/retrooper/packetevents/) and [EntityLib](https://github.com/Tofaa2/EntityLib) 

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
PetsAPI.init(this)

Player player = ...
PlayerPet pet = new PlayerPet(SkinData.ofPlayerName(player.getName()));
pet.spawn(player);

// Example Animal Head
Player player = ...
PlayerPet pet = new PlayerPet(AnimalSkinData.COW);
pet.setLookAtPlayer(true);
pet.spawn(player);

// Remove player pets
for (Pet pet : PetsAPI.getPets(player)) {
    pet.remove();
}

pet. // See all methods
``` 
