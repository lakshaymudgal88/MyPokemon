package com.example.mypokemon.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mypokemon.R
import com.example.mypokemon.data.remote.responses.Pokemon
import com.example.mypokemon.data.remote.responses.Type
import com.example.mypokemon.util.Resource
import com.example.mypokemon.util.parseStatToAbbr
import com.example.mypokemon.util.parseStatToColor
import com.example.mypokemon.util.parseTypeToColor
import com.example.mypokemon.viewmodel.PokemonInfoViewModel

@Composable
fun PokemonDetailScreen(
    navController: NavController,
    dominantColor: Color,
    pokemonName: String,
    pokemonInfoViewModel: PokemonInfoViewModel = hiltViewModel(),
    topPadding: Dp = 20.dp,
    imageSize: Dp = 200.dp,
) {

    val pokemonInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()) {
        value = pokemonInfoViewModel.getPokemonInfo(pokemonName)
    }.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dominantColor)
            .padding(bottom = 20.dp)
    ) {
        TopSection(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.2f)
                .align(Alignment.TopCenter)
        )
        PokemonInfoStateWrapper(
            pokemonInfo = pokemonInfo,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topPadding + imageSize / 2,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(color = MaterialTheme.colors.surface)
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .padding(
                    top = topPadding + imageSize / 2,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        )
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            if (pokemonInfo is Resource.Success) {
                pokemonInfo.data?.sprites?.let {
                    AsyncImage(
                        model = it.front_default,
                        contentDescription = pokemonInfo.data.name,
                        modifier = Modifier
                            .size(imageSize)
                            .offset(y = topPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TopSection(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier.background(
            brush = Brush.verticalGradient(
                listOf(
                    Color.Black,
                    Color.Transparent
                )
            )
        )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
    }
}

@Composable
fun PokemonInfoStateWrapper(
    pokemonInfo: Resource<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
) {
    when (pokemonInfo) {
        is Resource.Loading -> {
            CircularProgressIndicator(
                color = MaterialTheme.colors.primary,
                modifier = loadingModifier
            )
        }
        is Resource.Success -> {
            PokemonDetailsSection(
                pokemon = pokemonInfo.data!!,
                modifier = modifier.offset(y = (-20).dp)
            )
        }
        is Resource.Error -> {
            Text(
                text = pokemonInfo.message!!, color = Color.Red,
                modifier = modifier
            )
        }
    }
}

@Composable
fun PokemonDetailsSection(
    pokemon: Pokemon,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .offset(y = 100.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "#${pokemon.id} ${pokemon.name.capitalize(Locale("en"))}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface,
            fontSize = 30.sp
        )
        PokemonTypeSection(types = pokemon.types)
        PokemonDetailDataSection(pokemonWeight = pokemon.weight, pokemonHeight = pokemon.height)
        PokemonBaseStats(pokemon = pokemon)
    }
}

@Composable
fun PokemonTypeSection(
    types: List<Type>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        for (type in types) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clip(CircleShape)
                    .background(parseTypeToColor(type))
                    .height(35.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = type.type.name.capitalize(Locale("en")),
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PokemonDetailDataSection(
    pokemonWeight: Int,
    pokemonHeight: Int,
    sectionHeight: Dp = 80.dp
) {
    val pokemonWeightInKg = remember {
        (pokemonWeight * 100f) / 1000f
    }
    val pokemonHeightInMtr = remember {
        (pokemonHeight * 100f) / 1000f
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        PokemonDetailDataItem(
            dataValue = pokemonWeightInKg,
            dataUnit = " kg",
            dataIcon = painterResource(id = R.drawable.ic_weight),
            modifier = Modifier.weight(1f)
        )
        Spacer(
            modifier = Modifier
                .size(1.dp, sectionHeight)
                .background(color = Color.LightGray)
        )
        PokemonDetailDataItem(
            dataValue = pokemonHeightInMtr,
            dataUnit = " m",
            dataIcon = painterResource(id = R.drawable.ic_height),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PokemonDetailDataItem(
    dataValue: Float,
    dataUnit: String,
    dataIcon: Painter,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Icon(painter = dataIcon, contentDescription = null, tint = MaterialTheme.colors.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$dataValue$dataUnit",
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun PokemonStats(
    statName: String,
    statValue: Int,
    statMaxValue: Int,
    statColor: Color,
    height: Dp = 25.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val currPercent = animateFloatAsState(
        targetValue = if (animationPlayed) {
            statValue / statMaxValue.toFloat()
        } else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay
        )
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(
                color = if (isSystemInDarkTheme()) {
                    Color(0xFF505050)
                } else {
                    Color.LightGray
                }
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(currPercent.value)
                .clip(CircleShape)
                .background(statColor)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = statName, fontWeight = FontWeight.Bold
            )
            Text(
                text = (currPercent.value * statMaxValue).toInt().toString(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PokemonBaseStats(
    pokemon: Pokemon,
    initialAnimDelay: Int = 100
) {
    val baseMaxStat = remember {
        pokemon.stats.maxOf { it.base_stat }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Base Stats:",
            fontSize = 22.sp,
            color = MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        for (i in pokemon.stats.indices) {
            val stat = pokemon.stats[i]
            PokemonStats(
                statName = parseStatToAbbr(stat),
                statValue = stat.base_stat,
                statMaxValue = baseMaxStat,
                statColor = parseStatToColor(stat),
                animDelay = i * initialAnimDelay
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}














