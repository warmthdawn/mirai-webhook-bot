package com.warmthdawn.bot.webhookbot.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 *
 * @author WarmthDawn
 * @since 2021-07-06
 */
object EmojiUtils {
    private val types = "[\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83C\uDFA8\",\n" +
            "    \"code\": \":art:\",\n" +
            "    \"description\": \"Improving structure / format of the code.\",\n" +
            "    \"name\": \"style\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"⚡️\",\n" +
            "    \"code\": \":zap:\",\n" +
            "    \"description\": \"Improving performance.\",\n" +
            "    \"name\": \"perf\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDD25\",\n" +
            "    \"code\": \":fire:\",\n" +
            "    \"description\": \"Removing code or files.\",\n" +
            "    \"name\": \"prune\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDC1B\",\n" +
            "    \"code\": \":bug:\",\n" +
            "    \"description\": \"Fixing a bug.\",\n" +
            "    \"name\": \"fix\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDE91\",\n" +
            "    \"code\": \":ambulance:\",\n" +
            "    \"description\": \"Critical hotfix.\",\n" +
            "    \"name\": \"quickfix\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"✨\",\n" +
            "    \"code\": \":sparkles:\",\n" +
            "    \"description\": \"Introducing new features.\",\n" +
            "    \"name\": \"feature\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDCDD\",\n" +
            "    \"code\": \":pencil:\",\n" +
            "    \"description\": \"Writing docs.\",\n" +
            "    \"name\": \"docs\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDE80\",\n" +
            "    \"code\": \":rocket:\",\n" +
            "    \"description\": \"Deploying stuff.\",\n" +
            "    \"name\": \"deploy\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDC84\",\n" +
            "    \"code\": \":lipstick:\",\n" +
            "    \"description\": \"Updating the UI and style files.\",\n" +
            "    \"name\": \"ui\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83C\uDF89\",\n" +
            "    \"code\": \":tada:\",\n" +
            "    \"description\": \"Initial commit.\",\n" +
            "    \"name\": \"init\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"✅\",\n" +
            "    \"code\": \":white_check_mark:\",\n" +
            "    \"description\": \"Adding tests.\",\n" +
            "    \"name\": \"test\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDD12\",\n" +
            "    \"code\": \":lock:\",\n" +
            "    \"description\": \"Fixing security issues.\",\n" +
            "    \"name\": \"security\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83C\uDF4E\",\n" +
            "    \"code\": \":apple:\",\n" +
            "    \"description\": \"Fixing something on macOS.\",\n" +
            "    \"name\": \"osx\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDC27\",\n" +
            "    \"code\": \":penguin:\",\n" +
            "    \"description\": \"Fixing something on Linux.\",\n" +
            "    \"name\": \"linux\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83C\uDFC1\",\n" +
            "    \"code\": \":checkered_flag:\",\n" +
            "    \"description\": \"Fixing something on Windows.\",\n" +
            "    \"name\": \"windows\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83E\uDD16\",\n" +
            "    \"code\": \":robot:\",\n" +
            "    \"description\": \"Fixing something on Android.\",\n" +
            "    \"name\": \"android\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83C\uDF4F\",\n" +
            "    \"code\": \":green_apple:\",\n" +
            "    \"description\": \"Fixing something on iOS.\",\n" +
            "    \"name\": \"ios\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDD16\",\n" +
            "    \"code\": \":bookmark:\",\n" +
            "    \"description\": \"Releasing / Version tags.\",\n" +
            "    \"name\": \"release\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDEA8\",\n" +
            "    \"code\": \":rotating_light:\",\n" +
            "    \"description\": \"Removing linter warnings.\",\n" +
            "    \"name\": \"lint\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDEA7\",\n" +
            "    \"code\": \":construction:\",\n" +
            "    \"description\": \"Work in progress.\",\n" +
            "    \"name\": \"wip\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDC9A\",\n" +
            "    \"code\": \":green_heart:\",\n" +
            "    \"description\": \"Fixing CI Build.\",\n" +
            "    \"name\": \"fix-ci\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"⬇️\",\n" +
            "    \"code\": \":arrow_down:\",\n" +
            "    \"description\": \"Downgrading dependencies.\",\n" +
            "    \"name\": \"downgrade\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"⬆️\",\n" +
            "    \"code\": \":arrow_up:\",\n" +
            "    \"description\": \"Upgrading dependencies.\",\n" +
            "    \"name\": \"upgrade\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDCCC\",\n" +
            "    \"code\": \":pushpin:\",\n" +
            "    \"description\": \"Pinning dependencies to specific versions.\",\n" +
            "    \"name\": \"pushpin\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDC77\",\n" +
            "    \"code\": \":construction_worker:\",\n" +
            "    \"description\": \"Adding CI build system.\",\n" +
            "    \"name\": \"ci\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDCC8\",\n" +
            "    \"code\": \":chart_with_upwards_trend:\",\n" +
            "    \"description\": \"Adding analytics or tracking code.\",\n" +
            "    \"name\": \"analytics\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"♻️\",\n" +
            "    \"code\": \":recycle:\",\n" +
            "    \"description\": \"Refactoring code.\",\n" +
            "    \"name\": \"refactoring\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDC33\",\n" +
            "    \"code\": \":whale:\",\n" +
            "    \"description\": \"Work about Docker.\",\n" +
            "    \"name\": \"docker\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"➕\",\n" +
            "    \"code\": \":heavy_plus_sign:\",\n" +
            "    \"description\": \"Adding a dependency.\",\n" +
            "    \"name\": \"dep-add\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"➖\",\n" +
            "    \"code\": \":heavy_minus_sign:\",\n" +
            "    \"description\": \"Removing a dependency.\",\n" +
            "    \"name\": \"dep-rm\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDD27\",\n" +
            "    \"code\": \":wrench:\",\n" +
            "    \"description\": \"Changing configuration files.\",\n" +
            "    \"name\": \"config\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83C\uDF10\",\n" +
            "    \"code\": \":globe_with_meridians:\",\n" +
            "    \"description\": \"Internationalization and localization.\",\n" +
            "    \"name\": \"i18n\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"✏️\",\n" +
            "    \"code\": \":pencil2:\",\n" +
            "    \"description\": \"Fixing typos.\",\n" +
            "    \"name\": \"typo\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDCA9\",\n" +
            "    \"code\": \":poop:\",\n" +
            "    \"description\": \"Writing bad code that needs to be improved.\",\n" +
            "    \"name\": \"poo\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"⏪\",\n" +
            "    \"code\": \":rewind:\",\n" +
            "    \"description\": \"Reverting changes.\",\n" +
            "    \"name\": \"revert\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDD00\",\n" +
            "    \"code\": \":twisted_rightwards_arrows:\",\n" +
            "    \"description\": \"Merging branches.\",\n" +
            "    \"name\": \"merge\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDCE6\",\n" +
            "    \"code\": \":package:\",\n" +
            "    \"description\": \"Updating compiled files or packages.\",\n" +
            "    \"name\": \"dep-up\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDC7D\",\n" +
            "    \"code\": \":alien:\",\n" +
            "    \"description\": \"Updating code due to external API changes.\",\n" +
            "    \"name\": \"compat\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDE9A\",\n" +
            "    \"code\": \":truck:\",\n" +
            "    \"description\": \"Moving or renaming files.\",\n" +
            "    \"name\": \"mv\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDCC4\",\n" +
            "    \"code\": \":page_facing_up:\",\n" +
            "    \"description\": \"Adding or updating license.\",\n" +
            "    \"name\": \"license\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDCA5\",\n" +
            "    \"code\": \":boom:\",\n" +
            "    \"description\": \"Introducing breaking changes.\",\n" +
            "    \"name\": \"breaking\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83C\uDF71\",\n" +
            "    \"code\": \":bento:\",\n" +
            "    \"description\": \"Adding or updating assets.\",\n" +
            "    \"name\": \"assets\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDC4C\",\n" +
            "    \"code\": \":ok_hand:\",\n" +
            "    \"description\": \"Updating code due to code review changes.\",\n" +
            "    \"name\": \"review\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"♿️\",\n" +
            "    \"code\": \":wheelchair:\",\n" +
            "    \"description\": \"Improving accessibility.\",\n" +
            "    \"name\": \"access\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDCA1\",\n" +
            "    \"code\": \":bulb:\",\n" +
            "    \"description\": \"Documenting source code.\",\n" +
            "    \"name\": \"docs-code\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83C\uDF7B\",\n" +
            "    \"code\": \":beers:\",\n" +
            "    \"description\": \"Writing code drunkenly.\",\n" +
            "    \"name\": \"beer\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDCAC\",\n" +
            "    \"code\": \":speech_balloon:\",\n" +
            "    \"description\": \"Updating text and literals.\",\n" +
            "    \"name\": \"texts\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDDC3\",\n" +
            "    \"code\": \":card_file_box:\",\n" +
            "    \"description\": \"Performing database related changes.\",\n" +
            "    \"name\": \"db\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDD0A\",\n" +
            "    \"code\": \":loud_sound:\",\n" +
            "    \"description\": \"Adding logs.\",\n" +
            "    \"name\": \"log-add\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDD07\",\n" +
            "    \"code\": \":mute:\",\n" +
            "    \"description\": \"Removing logs.\",\n" +
            "    \"name\": \"log-rm\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDC65\",\n" +
            "    \"code\": \":busts_in_silhouette:\",\n" +
            "    \"description\": \"Adding contributor(s).\",\n" +
            "    \"name\": \"contrib-add\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDEB8\",\n" +
            "    \"code\": \":children_crossing:\",\n" +
            "    \"description\": \"Improving user experience / usability.\",\n" +
            "    \"name\": \"ux\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83C\uDFD7\",\n" +
            "    \"code\": \":building_construction:\",\n" +
            "    \"description\": \"Making architectural changes.\",\n" +
            "    \"name\": \"arch\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDCF1\",\n" +
            "    \"code\": \":iphone:\",\n" +
            "    \"description\": \"Working on responsive design.\",\n" +
            "    \"name\": \"iphone\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83E\uDD21\",\n" +
            "    \"code\": \":clown_face:\",\n" +
            "    \"description\": \"Mocking things.\",\n" +
            "    \"name\": \"clown-face\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83E\uDD5A\",\n" +
            "    \"code\": \":egg:\",\n" +
            "    \"description\": \"Adding an easter egg.\",\n" +
            "    \"name\": \"egg\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDE48\",\n" +
            "    \"code\": \":see_no_evil:\",\n" +
            "    \"description\": \"Adding or updating a .gitignore file.\",\n" +
            "    \"name\": \"see-no-evil\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDCF8\",\n" +
            "    \"code\": \":camera_flash:\",\n" +
            "    \"description\": \"Adding or updating snapshots.\",\n" +
            "    \"name\": \"camera-flash\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"⚗\",\n" +
            "    \"code\": \":alembic:\",\n" +
            "    \"description\": \"Experimenting new things.\",\n" +
            "    \"name\": \"experiment\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDD0D\",\n" +
            "    \"code\": \":mag:\",\n" +
            "    \"description\": \"Improving SEO.\",\n" +
            "    \"name\": \"seo\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"☸️\",\n" +
            "    \"code\": \":wheel_of_dharma:\",\n" +
            "    \"description\": \"Work about Kubernetes.\",\n" +
            "    \"name\": \"k8s\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83C\uDFF7️\",\n" +
            "    \"code\": \":label:\",\n" +
            "    \"description\": \"Adding or updating types (Flow, TypeScript).\",\n" +
            "    \"name\": \"types\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83C\uDF31\",\n" +
            "    \"code\": \":seedling:\",\n" +
            "    \"description\": \"Adding or updating seed files.\",\n" +
            "    \"name\": \"seed\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDEA9\",\n" +
            "    \"code\": \":triangular_flag_on_post:\",\n" +
            "    \"description\": \"Adding, updating, or removing feature flags.\",\n" +
            "    \"name\": \"flags\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"emoji\": \"\uD83D\uDCAB\",\n" +
            "    \"code\": \":dizzy:\",\n" +
            "    \"description\": \"Adding or updating animations and transitions.\",\n" +
            "    \"name\": \"animation\"\n" +
            "  }\n" +
            "]"

    private val emojiArray =
        Json.decodeFromString<Array<Emoji>>(this.types);

    val emojiMap = emojiArray.associate { it.code to it.emoji }
    val emojiCodes = emojiArray.map { it.code }

    fun getEmoji(code: String): String = emojiMap.getOrDefault(code, code)

    private val regex = Regex(":[\\w_]+:")
    fun processCommitMessage(message: String): String {
        return regex.replace(message) {
            getEmoji(it.value)
        }
    }

}

@Serializable
data class Emoji(
    val name: String,
    val emoji: String,
    val code: String,
    val description: String,
)