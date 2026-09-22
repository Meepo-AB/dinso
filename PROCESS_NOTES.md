# Process notes

How the work was done (tasks 1–4).

## Steps

1. Read the tasks and tried to understand the background and the needed behaviour.
2. Cloned the repository.
3. Went through the folders and files to get an idea of what parts we have.
4. Ran the project locally.
5. Went through the different parts and views of the app.
6. Pasted the tasks into Cursor and asked it to plan the steps of the implementation.
7. Went through what it recommended and reviewed it.
8. Answered some questions it provided about the design:
   - **What shape should the per-action model take?**  
     We decided on a new `CompanyAction` enum so we have a fixed list of the actions needed.
   - **How are actions stored per person?**  
     We created a table that connects profiles with the actions they have — one row for one profile and one action.
   - **Admin UI**  
     We extended `SystemAdminView` to include the new permissions instead of creating a new view, because it feels like they belong under the same umbrella.
9. The AI followed the plan between every implementation step to stay on track.
10. Tested the implementation manually.
11. Checked that we fulfilled the requested changes.
12. Created a code-review skill with these finding labels:

    | Label | Role |
    | --- | --- |
    | Must fix | Blocking — wrong, unsafe, or hard rule break |
    | Should fix | Important — clear issue; usually blocks unless accepted |
    | Nice to have | Optional polish — never blocks alone |

13. Committed the changes.
