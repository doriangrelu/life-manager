package io.github.doriangrelu.lifemanager.project.application.port.in;

import io.github.doriangrelu.lifemanager.project.application.port.in.query.ListProjectsQuery;
import io.github.doriangrelu.lifemanager.project.application.port.in.result.ProjectView;
import java.util.List;

/** Liste les projets du foyer. */
public interface ListProjects {

    List<ProjectView> list(ListProjectsQuery query);
}
