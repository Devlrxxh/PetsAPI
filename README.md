# PetsAPI
<div align="center">
</div>

# Introduction
Fully packet based PetsAPI (1.8 - 1.21) using [PacketEvents](https://github.com/retrooper/packetevents/) and [EntityLib](https://github.com/Tofaa2/EntityLib) 

# Setup
1. Clone repo
2. ```run mvn install```

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
