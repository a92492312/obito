package net.mcreator.odito.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class KamuiChunkGenerator extends ChunkGenerator {

    /** Минимальная высота вершины столба. */
    public static final int MIN_COLUMN_TOP = 40;
    /** Разброс высот: от MIN_COLUMN_TOP до MIN_COLUMN_TOP + VARIATION - 1. */
    public static final int VARIATION = 40;

    public static final Codec<KamuiChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource)
            ).apply(instance, KamuiChunkGenerator::new));

    private final BlockState columnBlock;

    public KamuiChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
        Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation("odito:kamui_block"));
        this.columnBlock = (block != null ? block : Blocks.STONE).defaultBlockState();
    }

    /** Детерминированный «хэш» чанка: всегда одинаков для одних и тех же координат. */
    public static int columnHash(int chunkX, int chunkZ) {
        long h = (long) chunkX * 0x2545F4914F6CDD1DL ^ (long) chunkZ * 0x9E3779B97F4A7C15L;
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        return (int) (h & 0x7FFFFFFFL);
    }

    /** Высота вершины столба для чанка (x, z). */
    public static int columnTop(int chunkX, int chunkZ) {
        return MIN_COLUMN_TOP + columnHash(chunkX, chunkZ) % VARIATION;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender,
            RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int top = columnTop(chunkPos.x, chunkPos.z);
        int minY = chunk.getMinBuildHeight();

        Heightmap surface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        Heightmap oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < top; y++) {
                    chunk.setBlockState(new BlockPos(x, y, z), this.columnBlock, false);
                }
                surface.update(x, top - 1, z, this.columnBlock);
                oceanFloor.update(x, top - 1, z, this.columnBlock);
            }
        }

        this.fillBiomes(chunk);
        return CompletableFuture.completedFuture(chunk);
    }

    /** Один биом на весь чанк, выбирается детерминированно из списка в biome_source. */
    private void fillBiomes(ChunkAccess chunk) {
        List<Holder<Biome>> biomes = new ArrayList<>(this.biomeSource.possibleBiomes());
        if (biomes.isEmpty()) {
            return;
        }
        biomes.sort(Comparator.comparing(h -> h.unwrapKey().map(k -> k.location().toString()).orElse("")));
        int index = columnHash(chunk.getPos().x, chunk.getPos().z) % biomes.size();
        Holder<Biome> biome = biomes.get(index);

        for (LevelChunkSection section : chunk.getSections()) {
            if (section == null) {
                continue;
            }
            // Контейнер биомов на деле всегда PalettedContainer — снимаем read-only «замок»
            PalettedContainer<Holder<Biome>> container =
                    (PalettedContainer<Holder<Biome>>) section.getBiomes();
            // Секция хранит биомы в сетке 4x4x4 (по 4 блока на клетку)
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    for (int k = 0; k < 4; k++) {
                        container.getAndSetUnchecked(i, j, k, biome);
                    }
                }
            }
        }
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState randomState) {
        return columnTop(x >> 4, z >> 4);
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState randomState) {
        int top = columnTop(x >> 4, z >> 4);
        int minY = level.getMinBuildHeight();
        int height = level.getHeight();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState[] states = new BlockState[height];
        for (int i = 0; i < height; i++) {
            states[i] = (minY + i < top) ? this.columnBlock : air;
        }
        return new NoiseColumn(minY, states);
    }

    @Override
    public int getGenDepth() {
        return 256;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState,
            BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk,
            GenerationStep.Carving step) {
        // Пусто: пещеры и иные вырезы не нужны
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager,
            RandomState randomState, ChunkAccess chunk) {
        // Пусто: вся поверхность уже заложена в fillFromNoise
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        // Мобы в измерении Камуи не спавнятся
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState randomState, BlockPos pos) {
        info.add("Kamui: столб высотой Y=" + columnTop(pos.getX() >> 4, pos.getZ() >> 4));
    }
}
